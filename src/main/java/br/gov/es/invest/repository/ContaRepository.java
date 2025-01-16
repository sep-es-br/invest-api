package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Conta;

public interface ContaRepository extends Neo4jRepository<Conta, String> {
    
    @Query("MATCH (conta:Conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\n" +
            "WHERE NOT EXISTS((conta)<-[:ORIENTA]-(:PlanoOrcamentario))\n" +
            "    AND unidade.codigo = $codUnidade\n" +
            "RETURN conta ")
    public Conta getGenericoByCodUnidade(String codUnidade);

    @Query("MATCH (unidadeOrcamentaria:UnidadeOrcamentaria)-[implemanta:IMPLEMENTA]->(conta:Conta)<-[orienta:ORIENTA]-(planoOrcamentario:PlanoOrcamentario),\r\n" + //
                "    (conta)<-[custeado:CUSTEADO]-(obj:Objeto)\r\n" + //
                "WHERE ( $unidadeId IS NULL OR elementId(unidadeOrcamentaria) = $unidadeId)\r\n" + //
                "    AND ( $planoId IS NULL OR elementId(planoOrcamentario) = $planoId)\r\n" + //
                "    AND ( $nome IS NULL OR apoc.text.clean(conta.nome) CONTAINS apoc.text.clean($nome))  \r\n" + //
                "OPTIONAL MATCH (conta)<-[delimita:DELIMITA]-(execucao:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
                "WHERE execucao.anoExercicio = $anoExercicio\r\n" + //
                "    AND ( $fonteId IS NULL OR elementId(fonteExec) = $fonteId)\r\n" + //
                "WITH  unidadeOrcamentaria, implemanta, orienta, \r\n" + //
                "        planoOrcamentario, custeado, obj, conta, delimita, \r\n" + //
                "        execucao, vinculada_por, fonteExec, count(fonteExec) as countFontExec\r\n" + //
                "OPTIONAL MATCH (obj)<-[estimado:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
                "WHERE custo.anoExercicio = $anoExercicio\r\n" + //
                "    AND ( $fonteId IS NULL OR elementId(fonteCusto) = $fonteId)\r\n" + //
                "WITH  unidadeOrcamentaria, implemanta, orienta, \r\n" + //
                "    planoOrcamentario, custeado, obj, conta, delimita, \r\n" + //
                "    execucao, vinculada_por, fonteExec, countFontExec,\r\n" + //
                "    estimado, custo, indicada_por, fonteCusto, count(fonteCusto) as countFontCusto\r\n" + //
                "WHERE ( $fonteId IS NULL OR countFontExec > 0 OR countFontCusto > 0)\r\n" + //
                "return conta, \r\n" + //
                "        collect(implemanta), collect(unidadeOrcamentaria), collect(orienta), collect(planoOrcamentario),\r\n" + //
                "        collect(custeado), collect(obj),\r\n" + //
                "        collect(delimita), collect(execucao), collect(vinculada_por), collect(fonteExec),\r\n" + //
                "        collect(estimado), collect(custo), collect(indicada_por), collect(fonteCusto)\r\n" + //
                "SKIP $skip LIMIT $limit")
    public List<Conta> getContaPorFiltro(String nome, String unidadeId, String planoId,
            Integer anoExercicio, String fonteId, Pageable pageable);

            @Query("MATCH (unidadeOrcamentaria:UnidadeOrcamentaria)-[implemanta:IMPLEMENTA]->(conta:Conta)<-[orienta:ORIENTA]-(planoOrcamentario:PlanoOrcamentario),\r\n" + //
            "    (conta)<-[custeado:CUSTEADO]-(obj:Objeto)\r\n" + //
            "WHERE ( $unidadeId IS NULL OR elementId(unidadeOrcamentaria) = $unidadeId)\r\n" + //
            "    AND ( $planoId IS NULL OR elementId(planoOrcamentario) = $planoId)\r\n" + //
            "    AND ( $nome IS NULL OR apoc.text.clean(conta.nome) CONTAINS apoc.text.clean($nome))  \r\n" + //
            "OPTIONAL MATCH (conta)<-[delimita:DELIMITA]-(execucao:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
            "WHERE execucao.anoExercicio = $anoExercicio\r\n" + //
            "    AND ( $fonteId IS NULL OR elementId(fonteExec) = $fonteId)\r\n" + //
            "WITH  unidadeOrcamentaria, implemanta, orienta, \r\n" + //
            "        planoOrcamentario, custeado, obj, conta, delimita, \r\n" + //
            "        execucao, vinculada_por, fonteExec, count(fonteExec) as countFontExec\r\n" + //
            "OPTIONAL MATCH (obj)<-[estimado:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
            "WHERE custo.anoExercicio = $anoExercicio\r\n" + //
            "    AND ( $fonteId IS NULL OR elementId(fonteCusto) = $fonteId)\r\n" + //
            "WITH  unidadeOrcamentaria, implemanta, orienta, \r\n" + //
            "    planoOrcamentario, custeado, obj, conta, delimita, \r\n" + //
            "    execucao, vinculada_por, fonteExec, countFontExec,\r\n" + //
            "    estimado, custo, indicada_por, fonteCusto, count(fonteCusto) as countFontCusto\r\n" + //
            "WHERE ( $fonteId IS NULL OR countFontExec > 0 OR countFontCusto > 0)\r\n" + //
            "return conta, \r\n" + //
            "        collect(implemanta), collect(unidadeOrcamentaria), collect(orienta), collect(planoOrcamentario),\r\n" + //
            "        collect(custeado), collect(obj),\r\n" + //
            "        collect(delimita), collect(execucao), collect(vinculada_por), collect(fonteExec),\r\n" + //
            "        collect(estimado), collect(custo), collect(indicada_por), collect(fonteCusto)\r\n")
    public List<Conta> getContaPorFiltro(String nome, String unidadeId, String planoId,
            Integer anoExercicio, String fonteId);

}
