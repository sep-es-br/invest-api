package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Papel;
import java.util.List;

public interface PapelRepository extends Neo4jRepository<Papel, String> {
    
    @Query("""
            MATCH (p:Papel)
            WHERE elementId(p) = $id
            DETACH DELETE p
           """)
    @Override
    public void deleteById(String id);
    
    @Query("""
            MATCH (p:Papel)
            WHERE elementId(p) IN $ids
            DETACH DELETE p
           """)
    public void deleteAllById(List<String> ids);

}
