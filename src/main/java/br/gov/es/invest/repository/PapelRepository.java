package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Papel;

public interface PapelRepository extends Neo4jRepository<Papel, Long> {
    
    @Query("MATCH (p:Papel)\r\n" + //
            "WHERE id(p) = $id\r\n" + //
            "DETACH DELETE p")
    public void deleteById(Long id);

}
