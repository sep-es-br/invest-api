package br.gov.es.invest.repository;

import br.gov.es.invest.model.Setor;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface SetorRepository extends Neo4jRepository<Setor, Long> {
    
    @Query("""
           MATCH (setor:Setor) 
           WHERE setor.guid = $guid 
           RETURN setor
           """)
    public Optional<Setor> findByGuid(String guid);

}
