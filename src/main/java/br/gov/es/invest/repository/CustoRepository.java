package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.IValoresCusto;
import br.gov.es.invest.model.Custo;

public interface CustoRepository extends Neo4jRepository<Custo, String> {
    
    @Query("MATCH (c:Custo)-[:EM]->(ano:Ano) WHERE (ano.ano = $exercicio) RETURN c\r\n")
    public List<Custo> findByExercicio(String exercicio);

    @Query("MATCH (custo:Custo)-[:EM]->(ano:Ano),\r\n" + //
            "    (fonte:FonteOrcamentaria)-[:INDICADA]->(custo)-[:ESTIMADO]->(p:Objeto)-[:CUSTEADO]->(conta:Conta)\r\n" + //
            "WHERE ( $idFonte IS NULL OR elementId(fonte) = $idFonte )\r\n" + //
            "    AND ano.ano = $exercicio\r\n" + //
            "OPTIONAL MATCH (conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r\n" + //
            "WHERE ( $idUnidade IS NULL OR elementId(unidade) = $idUnidade )\r\n" + //
            "OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ( $idPlano IS NULL OR elementId(plano) = $idPlano )\r\n" + //
            "RETURN custo")
    public List<IValoresCusto> getTotais(String idFonte, String exercicio, String idUnidade, String idPlano);

}
