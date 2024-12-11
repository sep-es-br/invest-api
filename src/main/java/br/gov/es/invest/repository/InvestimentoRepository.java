package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Investimento;

public interface InvestimentoRepository extends  Neo4jRepository<Investimento, String> {

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
                "RETURN conta, collect(rc), collect(obj), collect(re), collect(custo), collect(ri), collect(unidade),\r\n" + //
                "    collect(rd), collect(exec), collect(ro), collect(plano) SKIP $skip LIMIT $limit")
    public List<Investimento> findAllByFilter(
        String nome, String codUnidade, String codPO,
        String exercicio, String idFonte, Pageable pageable
    );

    @Query("MATCH \r\n" + //
            "    (ano:Ano)<-[:EM]-(exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano), \r\n" + //
            "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario),\r\n" + //
            "    (custo)<-[:INDICADA]-(fonte:FonteOrcamentaria)-[:VINCULA]->(exec)\r\n" + //
            "WHERE ($exercicio IS null OR ano.ano = $exercicio OR anoCusto.ano = $exercicio)   " + 
            "   AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))" + 
            "AND ($codPO IS NULL OR elementId(plano) = $codPO) " +
            "AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)" + 
            "    AND ($idFonte IS NULL OR elementId(fonte) = $idFonte)" + // 
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
    public Investimento getBycodUoPo(Long codUo, Long codPo);

    
    
} 
