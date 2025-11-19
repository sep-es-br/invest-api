/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.EmStatusDTO;
import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import br.gov.es.invest.dto.objeto.ObjetoDetailDto;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.AreaTematicaService;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.LocalidadeService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class ObjetoFactory {
    
    private final ObjetoService objSrv;
    private final UnidadeOrcamentariaService unidadeSrv;
    private final ContaService contaSrv;
    private final LocalidadeService localidadeSrv;
    private final AreaTematicaService areaSrv;
    private final InvestimentoService investimentoSrv;
    private final PlanoOrcamentarioService planoSrv;
    
    private final CustoFactory custoFactory;
        
    public ObjetoDetailDto fromModel(Objeto model) {
        return ObjetoDetailDto.builder()
                .id(model.getId())
                .tipoInvestimento(model.getConta().getClass().getSimpleName())
                .tipoObjeto(model.getTipo())
                .nome(model.getNome())
                .descricao(model.getDescricao())
                .codUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getCodigo())
                .siglaUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getSigla())
                .responsavel(model.getResponsavel().getNomeCompleto())
                .microrregiaoId(model.getMicrorregiao().getId())
                .microrregiaoNome(model.getMicrorregiao().getNome())
                .infoComplementar(model.getInfoComplementares())
                .codPlano(model.getConta().getPlanoOrcamentario().getCodigo())
                .nomePlano(model.getConta().getPlanoOrcamentario().getNome())
                .idArea(model.getAreaTematica().getId())
                .nomeArea(model.getAreaTematica().getNome())
                .contrato(model.getContrato())
                .tiposPlano(model.getTiposPlano().stream().map(
                        tpPlano -> new ObjetoDetailDto.TipoPlano(tpPlano.getId(), tpPlano.getNome(), tpPlano.getSigla())
                ).collect(Collectors.toList()))
                .custos(model.getCustosEstimadores().stream()
                    .collect(Collectors.toMap(
                        Custo::getAnoExercicio, 
                        custo -> custo.getIndicadaPor().stream()
                            .collect(Collectors.toMap(
                                    this::getCodFonte,
                                    this::from
                                ))
                        )))
                .emEtapa(EmEtapaDTO.parse(model.getEmEtapa()))
                .emStatus(EmStatusDTO.parse(model.getEmStatus()))
                .hashProposta(model.getHashProposta())
                .possuiOrcamento(model.getPossuiOrcamento())
                .build();
    }
    
    public Objeto fromDTO(ObjetoCadastroFormDto dto) {
        
        Objeto obj = Optional.ofNullable(dto.id())
                        .flatMap(id -> objSrv.getById(id))
                        .orElse(new Objeto());
                        
        obj.setTipo(dto.tipo());
        obj.setHashProposta(dto.hashProposta());
        obj.setNome(dto.nome());
        obj.setDescricao(dto.descricao());
        obj.setMicrorregiao(localidadeSrv.findById(dto.microregiaoId()).orElseThrow());
        obj.setInfoComplementares(dto.infoComplementares());
        
        List<TipoPlano> tiposPlanos = dto.planos().stream()
                                        .map(TipoPlano::new)
                                        .collect(Collectors.toList());
        
        obj.setTiposPlano(tiposPlanos);
        obj.setContrato(dto.contrato());
        obj.setAreaTematica(areaSrv.findById(dto.areaTematicaId()).orElseThrow());
        obj.setPossuiOrcamento(dto.possuiOrcamento());
        obj.setCustosEstimadoresFromDto(dto.recursos());
        
        UnidadeOrcamentaria unidade = unidadeSrv.findOrCreateByCod(dto.unidadeOrcamentaria());
        
        // define o Investimento que vai ser associado

        // se não tiver PO usa o investimento generico

        Conta conta;
        if(dto.planoOrcamentario() == null) {
            conta = contaSrv.getGenericoByCodUnidade(unidade);
        } else { // se não, busca o investimento

            Optional<Investimento> optInvestimento = investimentoSrv.getByCodUoPo(
                dto.unidadeOrcamentaria().codigo(), 
                dto.planoOrcamentario().codigo()
            );
            Investimento investimento;

            if(optInvestimento.isEmpty()){ // se não existir, cria um novo

                    PlanoOrcamentario plano = planoSrv.findOrCreateByCod(new PlanoOrcamentario(dto.planoOrcamentario()));

                    investimento = new Investimento();
                    investimento.setNome(dto.nome());
                    investimento.setUnidadeOrcamentariaImplementadora(unidade);
                    investimento.setPlanoOrcamentario(plano);
            } else { // se existir usa o existente
                investimento = optInvestimento.get();
            }
            
            conta = investimento;

        }
        
        obj.setConta(conta);
        
        obj.setCustosEstimadores(dto.recursos().stream().map(custoFactory::fromDto).collect(Collectors.toList()));
        
        return obj;
        
    }
    
    private ObjetoDetailDto.Custo from(IndicadaPor model) {
        return new ObjetoDetailDto.Custo(model.getPrevisto(), model.getContratado());
    }
    
    private String getCodFonte(IndicadaPor model) {
        return model.getFonteOrcamentaria().getCodigo();
    }
    
    
    
}
