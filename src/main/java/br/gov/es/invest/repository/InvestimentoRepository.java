package br.gov.es.invest.repository;

import br.gov.es.invest.model.Investimento;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface InvestimentoRepository extends  Neo4jRepository<Investimento, Long> {

    @Query("MATCH\r\n" + //
            "    (conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto),\r\n" + //
            "    (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ($codUnidade IS NULL OR id(unidade) = $codUnidade)\r\n" + //
            "    AND ($codPO IS NULL OR id(plano) = $codPO)\r\n" + //
            "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))\r\n" + //
            "OPTIONAL MATCH (obj)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
            "WHERE ($exercicio IS NULL OR custo.anoExercicio = $exercicio)\r\n" + //
            "    AND ($idFonte IS NULL OR id(fonteCusto) = $idFonte) \r\n" + //
            "OPTIONAL MATCH (conta)<-[rd:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vincula:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
            "WHERE ($exercicio IS null OR exec.anoExercicio = $exercicio) \r\n" + //
            "    AND ($idFonte IS NULL OR id(fonteExec) = $idFonte)\r\n" + //
            "RETURN conta, collect(rc), collect(obj),\r\n" + //
            "    collect(ri), collect(unidade),\r\n" + //
            "    collect(ro), collect(plano),\r\n" + //
            "    collect(re), collect(custo), collect(indicada), collect(fonteCusto),\r\n" + //
            "    collect(rd), collect(exec), collect(vincula), collect(fonteExec)" + //
            "SKIP $skip LIMIT $limit")
    public List<Investimento> findAllByFilter(
        String nome, Long codUnidade, Long codPO,
        Integer exercicio, Long idFonte, Pageable pageable
    );

    @Query("MATCH\r\n" + //
                "    (conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto),\r\n" + //
                "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                "WHERE ($codUnidade IS NULL OR id(unidade) = $codUnidade)\r\n" + //
                "    AND ($codPO IS NULL OR id(plano) = $codPO)\r\n" + //
                "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))\r\n" + //
                "OPTIONAL MATCH (obj)<-[re:ESTIMADO]-(custo:Custo),\r\n" + //
                "               (anoCusto:Ano)<-[:EM]-(custo)<-[:INDICADA]-(fonteCusto:FonteOrcamentaria)\r\n" + //
                "WHERE ($exercicio IS NULL OR anoCusto.ano = $exercicio)\r\n" + //
                "    AND ($idFonte IS NULL OR id(fonteCusto) = $idFonte) \r\n" + //
                "OPTIONAL MATCH (exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta),\r\n" + //
                "        (anoExec:Ano)<-[:EM]-(exec)<-[:VINCULA]-(fonteExec:FonteOrcamentaria)\r\n" + //
                "\r\n" + //
                "WHERE ($exercicio IS null OR anoExec.ano = $exercicio) \r\n" + //
                "    AND ($idFonte IS NULL OR id(fonteExec) = $idFonte)\r\n" + //
            "RETURN count(distinct conta)")
    public int countByFilter(String nome, Long codUnidade, Long codPO, String exercicio, Long idFonte);

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
                "WHERE ($planoId IS NULL OR id(plano) = $planoId)\r\n" + //
                "    AND ($unidadeId IS NULL OR id(unidade) = $unidadeId)\r\n" + //
                "RETURN conta\r\n" + //
                "ORDER BY unidade.codigo, plano.codigo\r\n" + //
                "SKIP $skip LIMIT $limit")
    public List<Investimento> findByUoPo(Long unidadeId, Long planoId, Pageable pageable);

    @Query("MATCH (investimento:Investimento)\r\n" + //
                "WHERE id(investimento) = $investimentoId\r\n" + //
                "WITH investimento\r\n" + //
                "MATCH (exec:ExecucaoOrcamentaria)\r\n" + //
                "WHERE id(exec) = $execId\r\n" + //
                "WITH investimento, exec\r\n" + //
                "CREATE (investimento)<-[:DELIMITA]-(exec)")
    public void addExecucao(Long investimentoId, Long execId);

    @Query(
            """
            MATCH (n:Conta)
            WHERE id(n) = $idInvestimento
            OPTIONAL MATCH (n)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)
            DETACH DELETE n, exec
            """
    )
    public void removerInvestimento(Long idInvestimento);
    
    
} 
