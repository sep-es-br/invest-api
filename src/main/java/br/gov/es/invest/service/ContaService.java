package br.gov.es.invest.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.DadoConsolidadoDTO;
import br.gov.es.invest.dto.DadosDetalhadoDTO;
import br.gov.es.invest.dto.DadosDetalhadoValores;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ContaRepository;
import br.gov.es.invest.utils.DataListResult;
import lombok.RequiredArgsConstructor;

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
                    Conta _conta = new Conta();
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
        Conta contaProbe = new Conta();
        
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

        String cypherBase = 
                        "WITH\r\n" + //
                        "    $tipoDespesa AS _tpDespesa,\r\n" + //
                        "    $gnd AS _gnd,\r\n" + //
                        "    $exercicio AS _exercicio,\r\n" + //
                        "    $idFonte AS _idFonte,\r\n" + //
                        "    $idsUnidade AS _idsUnidade,\r\n" + //
                        "    $idsPlano AS _idsPlano\r\n" + //
                        "MATCH \r\n" + //
                        "    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),\r\n" + //
                        "    (conta)<-[:CUSTEADO]-(obj:Objeto)<-[:ESTIMADO]-(custo:Custo),\r\n" + //
                        "    (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)\r\n" + //
                        "MATCH (fonte:FonteOrcamentaria)\r\n" + //
                        "WHERE\r\n" + //
                        "    _tpDespesa IN LABELS(conta)\r\n" + //
                        "    AND (_idFonte IS NULL OR elementId(fonte) = _idFonte)\r\n" + //
                        "    AND custo.anoExercicio = _exercicio\r\n" + //
                        "    AND (_idsUnidade IS NULL OR elementId(unidade) IN _idsUnidade)\r\n" + //
                        "    AND (_idsPlano IS NULL OR elementId(po) IN _idsPlano)\r\n" + //
                        "OPTIONAL MATCH (custo)-[indicada_por:INDICADA_POR]->(fonte)\r\n" + //
                        "WHERE (_gnd IS NULL OR indicada_por.gnd = _gnd)\r\n" + //
                        "WITH \r\n" + //
                        "    unidade,\r\n" + //
                        "    conta,\r\n" + //
                        "    po,\r\n" + //
                        "    obj,\r\n" + //
                        "    custo,\r\n" + //
                        "    tipoPlano,\r\n" + //
                        "    sum(indicada_por.previsto) AS valorPrevisto,\r\n" + //
                        "    sum(indicada_por.contratado) AS valorContratado,\r\n" + //
                        "    elementId(fonte) AS idFonte,\r\n" + //
                        "    fonte.nome AS nomeFonte\r\n" + //
                        "WITH \r\n" + //
                        "    unidade,\r\n" + //
                        "    conta,\r\n" + //
                        "    po,\r\n" + //
                        "    obj,\r\n" + //
                        "    custo,\r\n" + //
                        "    tipoPlano,\r\n" + //
                        "    collect({\r\n" + //
                        "        valorPrevisto: valorPrevisto,\r\n" + //
                        "        valorContratado: valorContratado,\r\n" + //
                        "        idFonte: idFonte,\r\n" + //
                        "        nomeFonte: nomeFonte\r\n" + //
                        "    }) AS valores\r\n";
        
        String cypherQuery = cypherBase + 
                            "RETURN\r\n" + //
                            "    unidade.codigo AS codUnidade,\r\n" + //
                            "    elementId(conta) AS idUnidade,\r\n" + //
                            "    unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,\r\n" + //
                            "    elementId(po) AS idPO,\r\n" + //
                            "    po.codigo AS codPO,\r\n" + //
                            "    po.nome AS nomePO,\r\n" + //
                            "    'E' IN collect(tipoPlano.sigla) AS projEstrategico,\r\n" + //
                            "    obj.contrato AS contrato,\r\n" + //
                            "    custo.anoExercicio AS anoExercicio,\r\n" + //
                            "    valores\r\n" + //
                            "ORDER BY codUnidade\r\n";

        
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            paramMap.put("skip", pageable.getOffset());
            paramMap.put("limit", pageable.getPageSize());
        }
        String cypherCount = cypherBase + "RETURN count(conta)";

        // List<DadosConsolidadosDTO> dados = neo4jOperations.findAll(cypherQuery, paramMap, DadosConsolidadosDTO.class);
        Collection<DadosDetalhadoDTO> dados = neo4jClient.query(cypherQuery).bindAll(paramMap)
        .fetchAs(DadosDetalhadoDTO.class)
        .mappedBy((typeSystem, record) -> new DadosDetalhadoDTO(
            record.get("idUnidade").asString(),
            record.get("unidadeResponsavel").asString(),
            record.get("idPO").asString(),
            record.get("codPO").asString(),
            record.get("nomePO").asString(),
            record.get("projEstrategico").asBoolean(),
            record.get("contrato").isNull() || record.get("contrato").isEmpty() ? "-" : record.get("contrato") .asString(),
            record.get("anoExercicio").asInt(),
            record.get("valores").asList(value -> new DadosDetalhadoValores(
                value.get("idFonte").asString(),
                value.get("nomeFonte").asString(),
                value.get("valorPrevisto").asDouble(),
                value.get("valorContratado").asDouble()
            ))
        ))
        .all();
        

        return new DataListResult<>(
            (List<DadosDetalhadoDTO>) dados, 
            (int) neo4jOperations.count(cypherCount, paramMap)
        );
    }

    public DataListResult<DadoConsolidadoDTO> getDadosConsolidados (
        String tipoDespesa, Integer gnd, Integer exercicioInicio, Integer exercicioFim, String idFonte,
        Pageable pageable, List<String> idsUnidade
    ) {


        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tipoDespesa", tipoDespesa);
        paramMap.put("gnd", gnd);
        paramMap.put("exercicioInicio", exercicioInicio);
        paramMap.put("exercicioFim", exercicioFim);
        paramMap.put("idFonte", idFonte);
        paramMap.put("idsUnidade", idsUnidade);

        String cypherBase = 
                        "MATCH (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)\r\n" + //
                                "WHERE \r\n" + //
                                "    $tipoDespesa IN labels(conta)\r\n" + //
                                "    AND ($idsUnidade IS NULL OR elementId(unidade) IN $idsUnidade)\r\n" + //
                                "    AND NOT EXISTS((obj)-[:EM]->(:Etapa))\r\n" + //
                                "\r\n" + //
                                "CALL {\r\n" + //
                                "    WITH obj\r\n" + //
                                "    MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
                                "    WHERE ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte)\r\n" + //
                                "        AND ($exercicioInicio <= custo.anoExercicio AND $exercicioFim >= custo.anoExercicio)\r\n" + //
                                "        AND ($gnd IS NULL OR indicada_por.gnd = $gnd)\r\n" + //
                                "    RETURN\r\n" + //
                                "        SUM(indicada_por.previsto) AS totalPrevisto,\r\n" + //
                                "        SUM(indicada_por.contratado) AS totalContratado\r\n" + //
                                "}\r\n" + //
                                "\r\n" + //
                                "CALL {\r\n" + //
                                "    WITH conta\r\n" + //
                                "    MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
                                "    WHERE ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)\r\n" + //
                                "        AND ($exercicioInicio <= exec.anoExercicio AND $exercicioFim >= exec.anoExercicio)\r\n" + //
                                "        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)\r\n" + //
                                "    RETURN\r\n" + //
                                "        SUM(vinculada_por.autorizado) AS totalAutorizado\r\n" + //
                                "}\r\n";
        
        String cypherQuery = cypherBase + 
                            "RETURN\r\n" + //
                            "    unidade.codigo AS codUnidade,\r\n" + //
                            "    unidade.codigo + ' - ' + unidade.sigla AS unidadeOperacional,\r\n" + //
                            "    COALESCE(SUM(totalPrevisto), 0) AS previsto,\r\n" + //
                            "    COALESCE(SUM(totalContratado), 0) AS contratado,\r\n" + //
                            "    COALESCE(SUM(totalAutorizado), 0) AS autorizado,\r\n" + //
                            "    COALESCE(SUM(totalAutorizado), 0) - COALESCE(SUM(totalContratado), 0) AS difAutorizadoContratado\r\n" + //
                            "ORDER BY codUnidade\r\n";

        
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            paramMap.put("skip", pageable.getOffset());
            paramMap.put("limit", pageable.getPageSize());
        }
        String cypherCount = cypherBase + "RETURN count(DISTINCT unidade)";

        Collection<DadoConsolidadoDTO> dados = neo4jClient.query(cypherQuery).bindAll(paramMap)
        .fetchAs(DadoConsolidadoDTO.class)
        .mappedBy((typeSystem, record) -> DadoConsolidadoDTO.builder()
                                            .unidadeOrcamentaria(record.get("unidadeOperacional").asString())
                                            .previsto(record.get("previsto").asDouble() )
                                            .contratado(record.get("contratado").asDouble())
                                            .autorizado(record.get("autorizado").asDouble())
                                            .difAutorizadoContratado(record.get("difAutorizadoContratado").asDouble())
                                            .build()
                                    )
        .all();
        

        return new DataListResult<>(
            (List<DadoConsolidadoDTO>) dados, 
            (int) neo4jOperations.count(cypherCount, paramMap)
        );
    }

}
