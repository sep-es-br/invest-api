package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.Objeto;

public interface ApontamentoRepository extends Neo4jRepository<Apontamento, String> {
    
    @Query("MATCH (a:Apontamento)\r\n" + //
                "MATCH (o:Objeto)\r\n" + //
                "WHERE elementId(a) = $apontamentoId AND elementId(o) = $objetoId\r\n" + //
                "MERGE (o)-[:POSSUI]->(a)")
    public void mergeObjetoApontamento(String objetoId, String apontamentoId);

}
