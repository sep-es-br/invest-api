package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Investimento;

public interface InvestimentoRepository extends  Neo4jRepository<Investimento, String> {

    @Query("MATCH\r\n" + //
            "    (conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto),\r\n" + //
            "    (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)\r\n" + //
            "    AND ($codPO IS NULL OR elementId(plano) = $codPO)\r\n" + //
            "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))\r\n" + //
            "OPTIONAL MATCH (obj)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
            "WHERE ($exercicio IS NULL OR custo.anoExercicio = $exercicio)\r\n" + //
            "    AND ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte) \r\n" + //
            "OPTIONAL MATCH (conta)<-[rd:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vincula:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
            "WHERE ($exercicio IS null OR exec.anoExercicio = $exercicio) \r\n" + //
            "    AND ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)\r\n" + //
            "RETURN conta, collect(rc), collect(obj),\r\n" + //
            "    collect(ri), collect(unidade),\r\n" + //
            "    collect(ro), collect(plano),\r\n" + //
            "    collect(re), collect(custo), collect(indicada), collect(fonteCusto),\r\n" + //
            "    collect(rd), collect(exec), collect(vincula), collect(fonteExec)" + //
            "SKIP $skip LIMIT $limit")
    public List<Investimento> findAllByFilter(
        String nome, String codUnidade, String codPO,
        Integer exercicio, String idFonte, Pageable pageable
    );

    @Query("MATCH\r\n" + //
                "    (conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto),\r\n" + //
                "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                "WHERE ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)\r\n" + //
                "    AND ($codPO IS NULL OR elementId(plano) = $codPO)\r\n" + //
                "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))\r\n" + //
                "OPTIONAL MATCH (obj)<-[re:ESTIMADO]-(custo:Custo),\r\n" + //
                "               (anoCusto:Ano)<-[:EM]-(custo)<-[:INDICADA]-(fonteCusto:FonteOrcamentaria)\r\n" + //
                "WHERE ($exercicio IS NULL OR anoCusto.ano = $exercicio)\r\n" + //
                "    AND ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte) \r\n" + //
                "OPTIONAL MATCH (exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta),\r\n" + //
                "        (anoExec:Ano)<-[:EM]-(exec)<-[:VINCULA]-(fonteExec:FonteOrcamentaria)\r\n" + //
                "\r\n" + //
                "WHERE ($exercicio IS null OR anoExec.ano = $exercicio) \r\n" + //
                "    AND ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)\r\n" + //
            "RETURN count(distinct conta)")
    public int countByFilter(String nome, String codUnidade, String codPO, String exercicio, String idFonte);

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[implementa:IMPLEMENTA]->(inv:Investimento)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario)<-[controla:CONTROLA]-(unidadePlano:UnidadeOrcamentaria),\r\n" + //
                "  (fonte:FonteOrcamentaria)-[vincula:VINCULA]->(execucao:ExecucaoOrcamentaria)-[em:EM]->(ano:Ano),\r\n" + //
                " (execucao)-[delimita:DELIMITA]->(inv)<-[custeado:CUSTEADO]-(objeto:Objeto)<-[estimado:ESTIMADO]-(custo:Custo),\r\n" + //
                " (fonteCusto:FonteOrcamentaria)-[indicada:INDICADA]->(custo)-[emCusto:EM]->(anoCusto:Ano)\r\n" + //
                " WHERE unidade.codigo = $codUo AND plano.codigo = $codPo\r\n" + //
                "RETURN inv, collect(unidade), collect(implementa), collect(orienta), \r\n" + //
                "collect(plano), collect(fonte), collect(vincula), \r\n" + //
                " collect(execucao), collect(em), collect(ano), \r\n" + //
                " collect(delimita), collect(custeado), collect(objeto), collect(estimado), collect(custo), collect(fonteCusto), collect(indicada), collect(emCusto), collect(anoCusto), collect(controla), collect(unidadePlano) LIMIT 1")
    public Investimento getBycodUoPo(String codUo, String codPo);

    @Query("MATCH (plano:PlanoOrcamentario)-[:ORIENTA]->(conta:Investimento)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r\n" + //
                "WHERE ($planoId IS NULL OR elementId(plano) = $planoId)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
                "RETURN conta\r\n" + //
                "ORDER BY unidade.codigo, plano.codigo\r\n" + //
                "SKIP $skip LIMIT $limit")
    public List<Investimento> findByUoPo(String unidadeId, String planoId, Pageable pageable);

    @Query("MATCH (investimento:Investimento)\r\n" + //
                "WHERE elementId(investimento) = $investimentoId\r\n" + //
                "WITH investimento\r\n" + //
                "MATCH (exec:ExecucaoOrcamentaria)\r\n" + //
                "WHERE elementId(exec) = $execId\r\n" + //
                "WITH investimento, exec\r\n" + //
                "CREATE (investimento)<-[:DELIMITA]-(exec)")
    public void addExecucao(String investimentoId, String execId);


    
    
} 
