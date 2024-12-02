package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Investimento;

public interface InvestimentoRepository extends  Neo4jRepository<Investimento, String> {

    @Query("MATCH\r\n" + //
            "    (ano:Ano)<-[:EM]-(exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano), \r\n" + //
            "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario),\r\n" + //
            "    (custo)<-[:INDICADA]-(fonte:FonteOrcamentaria)-[:VINCULA]->(exec)\r\n" + //
            "WHERE ($exercicio IS null OR ano.ano = $exercicio OR anoCusto.ano = $exercicio) \r\n" + //
            "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))\r\n" + //
            "    AND ($codPO IS NULL OR elementId(plano) = $codPO)\r\n" + //
            "    AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)\r\n" + //
            "    AND ($idFonte IS NULL OR elementId(fonte) = $idFonte)" + //
            "RETURN conta, collect(rc), collect(obj), collect(re), collect(custo), collect(ri), collect(unidade), \r\n" + //
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

    
    
} 
