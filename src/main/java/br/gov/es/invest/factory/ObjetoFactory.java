/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.EmStatusDTO;
import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import br.gov.es.invest.dto.objeto.ObjetoDetailDto;
import br.gov.es.invest.dto.objeto.ObjetoTiraSimplesDto;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.AreaTematica;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Localidade;
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
import br.gov.es.invest.utils.DateTimeUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class ObjetoFactory {
    
    private final Neo4jOperations neo4jOperations;
    
    private final ObjetoService objSrv;
    private final UnidadeOrcamentariaService unidadeSrv;
    private final ContaService contaSrv;
    private final LocalidadeService localidadeSrv;
    private final AreaTematicaService areaSrv;
    private final InvestimentoService investimentoSrv;
    private final PlanoOrcamentarioService planoSrv;
    
    private final CustoFactory custoFactory;
    private final EmEtapaFactory emEtapaFactory;
    private final RevisadoPorFactory revisadoFactory;
        
    public ObjetoDetailDto fromModel(Objeto model) {
        return ObjetoDetailDto.builder()
                .id(model.getId())
                .gnd(model.getGnd())
                .tipoInvestimento(Optional.ofNullable(model.getConta().getTipoConta()).orElse(Conta.TIPO_CONTA.INVESTIMENTO).toString())
                .tipoObjeto(model.getTipo())
                .nome(model.getNome())
                .descricao(model.getDescricao())
                .codUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getCodigo())
                .siglaUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getSigla())
                .responsavel(Optional.ofNullable(model.getResponsavel()).map((Agente::getNomeCompleto)).orElse(null))
                .microrregiaoId(Optional.ofNullable(model.getMicrorregiao()).map(Localidade::getId).orElse(null))
                .microrregiaoNome(Optional.ofNullable(model.getMicrorregiao()).map(Localidade::getNome).orElse(null))
                .infoComplementar(model.getInfoComplementares())
                .codPlano(Optional.ofNullable(model.getConta().getPlanoOrcamentario()).map(PlanoOrcamentario::getCodigo).orElse(null))
                .nomePlano(Optional.ofNullable(model.getConta().getPlanoOrcamentario()).map(PlanoOrcamentario::getNome).orElse(null))
                .idArea(Optional.ofNullable(model.getAreaTematica()).map(AreaTematica::getId).orElse(null))
                .nomeArea(Optional.ofNullable(model.getAreaTematica()).map(AreaTematica::getNome).orElse(null))
                .contrato(model.getContrato())
                .tiposPlano(model.getTiposPlano().stream().map(
                        tpPlano -> new ObjetoDetailDto.TipoPlano(tpPlano.getId(), tpPlano.getNome(), tpPlano.getSigla())
                ).collect(Collectors.toList()))
                .custos(
                    model.getCustosEstimadores().stream()
                        .collect(Collectors.toMap(
                            Custo::getAnoExercicio,
                            custo -> custo.getIndicadaPor().stream()
                                .collect(Collectors.toMap(
                                    this::getCodFonte,
                                    this::from,
                                    (c1, c2) ->
                                        new br.gov.es.invest.dto.objeto.ObjetoDetailDto.Custo(
                                            c1.planejado() + c2.planejado(),
                                            c1.contratado() + c2.contratado()
                                        )
                                )),
                            (map1, map2) -> {  // ← MERGE DO ANO DUPLICADO
                                map2.forEach((key, value) ->
                                    map1.merge(
                                        key,
                                        value,
                                        (c1, c2) -> new br.gov.es.invest.dto.objeto.ObjetoDetailDto.Custo(
                                            c1.planejado() + c2.planejado(),
                                            c1.contratado() + c2.contratado()
                                        )
                                    )
                                );
                                return map1;
                            }
                        ))
                )
                .emEtapa(Optional.ofNullable(model.getEmEtapa()).map(l -> l.stream().map(this.emEtapaFactory::toDto).toList()).orElse(null))
                .emStatus(EmStatusDTO.parse(model.getEmStatus()))
                .hashProposta(model.getHashProposta())
                .possuiOrcamento(model.getPossuiOrcamento())
                .timestamp(Optional.ofNullable(model.getTimestamp()).map(DateTimeUtils::formatZonedDateTime).orElse(null))
                .revisor(this.revisadoFactory.toDto(model.getRevisor()))
                .build();
    }
    
    public Objeto fromDTO(ObjetoCadastroFormDto dto, Conta conta) {
        
        Objeto obj = Optional.ofNullable(dto.id())
                        .flatMap(id -> objSrv.getById(id))
                        .orElse(new Objeto());
                        
        obj.setTipo(dto.tipo());
        obj.setGnd(dto.gnd());
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

        if(conta == null) {
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
        }   
        
        
        obj.setConta(conta);
        
        
        return obj;
        
    }
    
    public Objeto fromDTO(ObjetoCadastroFormDto dto) {
        
        return this.fromDTO(dto, null);
        
    }
    
    public ObjetoTiraSimplesDto gerarTiraSimples(Long id) {
        String cypher = """
                        MATCH (objeto:Objeto)-[:CUSTEADO]-(conta:Conta)
                        WHERE id(objeto) = $id
                        CALL (objeto) {
                          MATCH (objeto)-[]-(:Custo)-[vlr]->(:FonteOrcamentaria)
                          RETURN
                            sum(vlr.planejado) as planejado,
                            sum(vlr.contratado) as contratado
                        }
                        RETURN 
                          id(objeto) as id,
                          objeto.nome as nome,
                          planejado,
                          contratado
                        """;
        
        HashMap<String, Object> param = new HashMap<>();
        param.put("id", id);
        
        return this.neo4jOperations.findOne(cypher, param, ObjetoTiraSimplesDto.class).orElseThrow();
    }
    
    private ObjetoDetailDto.Custo from(IndicadaPor model) {
        return new ObjetoDetailDto.Custo(model.getPlanejado(), model.getContratado());
    }
    
    private String getCodFonte(IndicadaPor model) {
        return model.getFonteOrcamentaria().getCodigo();
    }
    
    
    
    
}
