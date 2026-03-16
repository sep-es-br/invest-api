package br.gov.es.invest.service;

import br.gov.es.invest.dto.DadoConsolidadoDTO;
import br.gov.es.invest.dto.DadosDetalhadoDTO;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ContaRepository;
import br.gov.es.invest.utils.DataListResult;
import br.gov.es.invest.utils.FunctionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContaService {

    
    private final ContaRepository repository;

    private final Neo4jOperations neo4jOperations;

    private final Neo4jClient neo4jClient;

    public Conta getGenericoByCodUnidade(UnidadeOrcamentaria unidadeOrcamentaria) {
                
        Conta conta = repository.getGenericoByCodUnidade(unidadeOrcamentaria.getCodigo());
        
        return Optional.ofNullable(conta)
                .map(_conta -> repository.findById(_conta.getId()).get())
                .orElseGet(() -> {
                    Conta _conta = new Conta(null);
                    _conta.setNome("Conta sem PO da Unidade " + unidadeOrcamentaria.getCodigo());
                    _conta.setUnidadeOrcamentariaImplementadora(unidadeOrcamentaria);
                    return _conta;
                });

    }

    public Conta save(Conta conta){
        return repository.save(conta);
    }

    public List<Conta> findByFiltro(
        String nome, Long unidadeId, Long planoId,
        Integer anoExercicio, Long fonteId, Pageable pageable
    ){
        
        ExampleMatcher matcher = ExampleMatcher.matching();
        Conta contaProbe = new Conta(null);
        
        if(nome != null){
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        }

        if(planoId != null){ 
            contaProbe.setPlanoOrcamentario(
                PlanoOrcamentario.builder()
                .id(planoId)
                .build()
            );
        } 

        if(unidadeId != null){
            contaProbe.setUnidadeOrcamentariaImplementadora(
                UnidadeOrcamentaria.builder()
                .id(unidadeId)
                .build()
            );
        }

        List<Conta> result = repository.findAll(Example.of(contaProbe, matcher));

        if(pageable != null){

            long indexTo = Long.min(pageable.getOffset()+pageable.getPageSize(), result.size()-pageable.getOffset());

            result = result
            .subList(Integer.parseInt(String.valueOf(pageable.getOffset())) , Integer.parseInt( String.valueOf(indexTo)));
        }

        result = repository.filtrarContasForaProcessamento(result.stream().map(c -> c.getId()).toList());

        return repository.findAllById(result.stream().map(c -> c.getId()).toList()) ;



    }

    public Integer countByFilter(String nome, Long codUnidade, Long codPO, Integer exercicio, Long idFonte){
        return findByFiltro(nome, codUnidade, codPO, exercicio, idFonte, null).size();
    }

    public DataListResult<DadosDetalhadoDTO> getDadosDetalhados (
        String tipoDespesa, Integer gnd, Integer exercicio, Long idFonte,
        Pageable pageable, List<Long> idsUnidade, List<Long> idsPlano
    ) {
        
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tipoDespesa", tipoDespesa);
        paramMap.put("gnd", gnd);
        paramMap.put("exercicio", exercicio);
        paramMap.put("idFonte", idFonte);
        paramMap.put("idsUnidade", idsUnidade);
        paramMap.put("idsPlano", idsPlano);

        String cypherBase = """
                            WITH
                                $tipoDespesa AS _tpDespesa,
                                $gnd AS _gnd,
                                $exercicio AS _exercicio,
                                $idFonte AS _idFonte,
                                $idsUnidade AS _idsUnidade,
                                $idsPlano AS _idsPlano
                            MATCH 
                                (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),
                                (conta)<-[:CUSTEADO]-(obj:Objeto)<-[:ESTIMADO]-(custo:Custo),
                                (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)
                            MATCH (fonte:FonteOrcamentaria)
                            WHERE
                                _tpDespesa IN LABELS(conta)
                                AND (_idFonte IS NULL OR id(fonte) = _idFonte)
                                AND custo.anoExercicio = _exercicio
                                AND (_idsUnidade IS NULL OR id(unidade) IN _idsUnidade)
                                AND (_idsPlano IS NULL OR id(po) IN _idsPlano)
                            OPTIONAL MATCH (custo)-[indicada_por:INDICADA_POR]->(fonte)
                            WHERE (_gnd IS NULL OR obj.gnd = _gnd)
                            WITH 
                                unidade,
                                conta,
                                po,
                                obj,
                                custo,
                                tipoPlano,
                                sum(indicada_por.planejado) AS valorPlanejado,
                                sum(indicada_por.contratado) AS valorContratado,
                                id(fonte) AS idFonte,
                                fonte.nome AS nomeFonte
                            WITH 
                                unidade,
                                conta,
                                po,
                                obj,
                                custo,
                                collect(tipoPlano.sigla) AS tipoPlano,
                                collect(DISTINCT {
                                    valorPlanejado: valorPlanejado,
                                    valorContratado: valorContratado,
                                    idFonte: idFonte,
                                    nomeFonte: nomeFonte
                                }) AS valores                            
                            """;
        
        String cypherQuery = cypherBase + 
                                """
                                RETURN {
                                    codUnidade: unidade.codigo,
                                    idUnidade: id(conta),
                                    unidadeResponsavel: unidade.codigo + ' - ' + unidade.sigla,
                                    idPO: id(po),
                                    codPO: po.codigo,
                                    nomePO: po.nome,
                                    projEstrategico: 'E' IN tipoPlano,
                                    contrato: obj.contrato,
                                    anoExercicio: custo.anoExercicio,
                                    valores: valores
                                } AS row
                                """;
        
        
        if(pageable != null) {
            cypherQuery = FunctionUtils.aplicarPageable(cypherQuery, pageable, paramMap);
        }
        String cypherCount = cypherBase + "RETURN count(conta)";

        // List<DadosConsolidadosDTO> dados = neo4jOperations.findAll(cypherQuery, paramMap, DadosConsolidadosDTO.class);
        Collection<DadosDetalhadoDTO> dados = neo4jClient.query(cypherQuery).bindAll(paramMap)
        .fetchAs(DadosDetalhadoDTO.class).mappedBy((typeSystem, record) -> new ObjectMapper().convertValue(record.get("row").asMap(), DadosDetalhadoDTO.class)).all();
        
        
        return new DataListResult<>(
            (List<DadosDetalhadoDTO>) dados, 
            (int) neo4jOperations.count(cypherCount, paramMap)
        );
    }

    public DataListResult<DadoConsolidadoDTO> getDadosConsolidados (
        String tipoDespesa, Integer gnd, Integer exercicioInicio, Integer exercicioFim, String idFonte,
        Pageable pageable, List<Long> idsUnidade
    ) {


        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tipoDespesa", tipoDespesa);
        paramMap.put("gnd", gnd);
        paramMap.put("exercicioInicio", exercicioInicio);
        paramMap.put("exercicioFim", exercicioFim);
        paramMap.put("idFonte", idFonte);
        paramMap.put("idsUnidade", idsUnidade);

        String cypherBase = 
                        """
                        MATCH (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(:Status{statusId: 'CADASTRADO'})
                        WHERE 
                            $tipoDespesa IN labels(conta)
                            AND ($idsUnidade IS NULL OR id(unidade) IN $idsUnidade)
                        
                        CALL {
                            WITH obj
                            MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                            WHERE ($idFonte IS NULL OR id(fonteCusto) = $idFonte)
                                AND ($exercicioInicio <= custo.anoExercicio AND $exercicioFim >= custo.anoExercicio)
                                AND ($gnd IS NULL OR obj.gnd = $gnd)
                            RETURN
                                SUM(indicada_por.planejado) AS totalPlanejado,
                                SUM(indicada_por.contratado) AS totalContratado
                        }
                        
                        CALL {
                            WITH conta
                            MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                            WHERE ($idFonte IS NULL OR id(fonteExec) = $idFonte)
                                AND ($exercicioInicio <= exec.anoExercicio AND $exercicioFim >= exec.anoExercicio)
                                AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                            RETURN\r
                                SUM(vinculada_por.autorizado) AS totalAutorizado
                        }\r
                        """ 
        ;
        
        String cypherQuery = cypherBase + 
                                """
                                RETURN DISTINCT {
                                    codUnidade: unidade.codigo,
                                    unidadeOrcamentaria: unidade.codigo + ' - ' + unidade.sigla,
                                    planejado: COALESCE(SUM(totalPlanejado), 0),
                                    contratado: COALESCE(SUM(totalContratado), 0),
                                    autorizado: COALESCE(SUM(totalAutorizado), 0),
                                    difAutorizadoContratado: COALESCE(SUM(totalAutorizado), 0) - COALESCE(SUM(totalContratado), 0)
                                } AS row
                                """;
        
        cypherQuery = FunctionUtils.aplicarPageable(cypherQuery, pageable, paramMap);

        String cypherCount = cypherBase + "RETURN count(DISTINCT unidade)";

        Collection<DadoConsolidadoDTO> dados = neo4jClient.query(cypherQuery).bindAll(paramMap)
        .fetchAs(DadoConsolidadoDTO.class)
        .mappedBy((typeSystem, record) -> 
                new ObjectMapper().convertValue(record.get("row").asMap(), DadoConsolidadoDTO.class)
        )
        .all();
        

        return new DataListResult<>(
            (List<DadoConsolidadoDTO>) dados, 
            (int) neo4jOperations.count(cypherCount, paramMap)
        );
    }

}
