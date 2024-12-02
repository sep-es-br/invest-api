package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Objeto;

public interface ObjetoRepository extends Neo4jRepository<Objeto, String> {
    

    @Query("MATCH (execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano),\r\n" + //
                "     (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta),\r\n" + //
                "    (ano:Ano)<-[:EM]-(execucao)\r\n" + //
                "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                "    AND ($execicio IS NULL OR ano.ano = $execicio OR anoCusto.ano = $execicio)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)" + //
                "RETURN obj, collect(re), collect(rd), collect(execucao), collect(custo), collect(ri), collect(unidade), collect(rc), collect(conta) SKIP $skip LIMIT $limit")
    public List<Objeto> getAllByFilter(String execicio, String nome, String unidadeId, String status, Pageable pageable); 


    @Query("MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) WHERE elementId(custo) = $custoId RETURN obj")
    public Objeto getByCusto(String custoId);

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:Em]->(anoCusto:Ano),\r\n" + //
            "    (ano:Ano)<-[:EM]-(execucao)" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
            "    AND ($execicio IS NULL OR ano.ano = $execicio OR anoCusto.ano = $execicio)" + //
            "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
            "  RETURN count(distinct obj)")
    public int ammountByFilter(String execicio, String nome, String unidadeId, String status);

    @Query("MATCH \r\n" + //
            "    (ano:Ano)<-[:EM]-(exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano), \r\n" + //
            "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ($exercicio IS null OR ano.ano = $exercicio OR anoCusto.ano = $exercicio)   " + 
            "   AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))" + 
            "AND ($codPO IS NULL OR elementId(plano) = $codPO) " +
            "AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)" +  
            "RETURN count(distinct obj)")
    public int countByInvestimentoFilter(String nome, String codUnidade, String codPO, String exercicio);
}
