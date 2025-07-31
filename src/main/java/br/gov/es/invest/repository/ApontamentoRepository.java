package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Apontamento;

public interface ApontamentoRepository extends Neo4jRepository<Apontamento, Long> {
    
    @Query("MATCH (a:Apontamento)\r\n" + //
                "MATCH (o:Objeto)\r\n" + //
                "WHERE id(a) = $apontamentoId AND id(o) = $objetoId\r\n" + //
                "MERGE (o)-[:POSSUI]->(a)")
    public void mergeObjetoApontamento(Long objetoId, Long apontamentoId);

}
