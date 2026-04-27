/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.investimento.InvestimentoCadastroDto;
import br.gov.es.invest.dto.investimento.InvestimentoDetailDto;
import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.FluxoService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.StatusService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class InvestimentoFactory {
    
    @Autowired
    private ObjetoService objSrv;
    
    @Autowired
    private ObjetoFactory objFactory;
    
    @Autowired
    private InvestimentoService investimentoSrv;
    
    @Autowired
    private UnidadeOrcamentariaService unidadeSrv;
    
    @Autowired
    private PlanoOrcamentarioService planoSrv;
    
    @Autowired
    private FluxoService fluxoService;
    
    @Autowired
    private StatusService statusService;
    
    
    public InvestimentoDetailDto toInvestimentoDetalDto(Investimento model){
        if(model == null) return null;
        
        return InvestimentoDetailDto.builder()
                .id(model.getId())
                .tipo(model.getTipoConta().toString())
                .nome(Optional.ofNullable(model.getNome()).orElse(model.getPlanoOrcamentario().getNome()))
                .descricao(model.getDescricao())
                .codUnidade(model.getUnidadeOrcamentariaImplementadora().getCodigo())
                .codPO(model.getPlanoOrcamentario().getCodigo())
                .objetos(this.objSrv.findObjetoByConta(model.getId()).stream().map(this.objFactory::fromModel).collect(Collectors.toList()))
                .build();
    }
    
    public Investimento toInvestimento(InvestimentoCadastroDto cadastroDto, Agente usuarioAtual) {
        
        if(cadastroDto == null) return null;
        
        UnidadeOrcamentaria unidade = this.unidadeSrv.findOrCreateByCod(
                UnidadeOrcamentaria.builder()
                        .codigo(cadastroDto.codUnidade())
                        .sigla(cadastroDto.siglaUnidade())
                        .build()
        );
        
        PlanoOrcamentario plano = this.planoSrv.findOrCreateByCod(
                PlanoOrcamentario.builder()
                .codigo(cadastroDto.codPo())
                .nome(cadastroDto.nomePo())
                .build()
        );
        
        Investimento investimentoParcial = cadastroDto.id() != null
                                            ? this.investimentoSrv.getById(cadastroDto.id()).orElseThrow()
                                            : this.investimentoSrv.getByCodUoPo(cadastroDto.codUnidade(), cadastroDto.codPo()).orElse(new Investimento());
        
        investimentoParcial.setTipoConta(Conta.TIPO_CONTA.of(cadastroDto.tipo()));
        investimentoParcial.setNome(cadastroDto.nome());
        investimentoParcial.setDescricao(cadastroDto.descricao());
        if(investimentoParcial.getUnidadeOrcamentariaImplementadora() == null) investimentoParcial.setUnidadeOrcamentariaImplementadora(unidade);
        if(investimentoParcial.getPlanoOrcamentario() == null) investimentoParcial.setPlanoOrcamentario(plano);
        
        ArrayList<Objeto> objs = new ArrayList<>();
        
        for(ObjetoCadastroFormDto objCadastro : cadastroDto.objetos()) {
            Objeto novo = objFactory.fromDTO(objCadastro, investimentoParcial);
            
            if(novo.getEmStatus() == null) {
            
                Status novoStatus = statusService.getByStatusId(StatusEnum.SOLICITADO.name()).get();

                EmStatus emStatus = new EmStatus();

                emStatus.setStatus(novoStatus);
                emStatus.setTimestamp(ZonedDateTime.now());

                novo.setEmStatus(emStatus);

                Fluxo fluxo = fluxoService.findByFluxoId("avaliacaoPip");

                EmEtapa emEtapa = new EmEtapa();
                emEtapa.setAtividade("Avaliar Solicitação");
                emEtapa.setDevolvido(false);
                emEtapa.setEtapa(fluxo.getEtapaInicial());

                novo.getEmEtapa().add(emEtapa);

            }
            
            
            if(novo.getResponsavel() == null) {
                novo.setResponsavel(usuarioAtual);
            }
            
            objs.add(novo);
        }
        
        investimentoParcial.setObjetos(objs);
        
        return investimentoParcial;
        
    }
    
}
