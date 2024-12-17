package br.gov.es.invest.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.IValoresCusto;
import br.gov.es.invest.model.Custo;

public interface CustoRepository extends Neo4jRepository<Custo, String> {
    
    @Query("MATCH (c:Custo)-[:EM]->(ano:Ano) WHERE (ano.ano = $exercicio) RETURN c\r\n")
    public List<Custo> findByExercicio(String exercicio);

    @Query("MATCH\r\n" + //
            "    (fonte:FonteOrcamentaria)<-[indicadaPor:INDICADA_POR]-(custo:Custo)-[:ESTIMADO]->(p:Objeto)-[:CUSTEADO]->(conta:Conta)\r\n" + //
            "WHERE ( $idFonte IS NULL OR elementId(fonte) = $idFonte )\r\n" + //
            "    AND custo.anoExercicio = $exercicio\r\n" + //
            "OPTIONAL MATCH (conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r\n" + //
            "WHERE ( $idUnidade IS NULL OR elementId(unidade) = $idUnidade )\r\n" + //
            "OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ( $idPlano IS NULL OR elementId(plano) = $idPlano )\r\n" + //
            "RETURN custo, collect(indicadaPor), collect(fonte)")
    public List<Custo> getTotais(String idFonte, Integer exercicio, String idUnidade, String idPlano);

    @Query("MATCH (custo:Custo)\r\n" + //
            "RETURN DISTINCT custo.anoExercicio")
    public Set<Integer> getAnosExercicio();
    
}
