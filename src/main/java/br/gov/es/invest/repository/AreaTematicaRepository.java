package br.gov.es.invest.repository;

import br.gov.es.invest.model.AreaTematica;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AreaTematicaRepository extends Neo4jRepository<AreaTematica, Long> {
    
    

}
