package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.AreaTematica;

public interface AreaTematicaRepository extends Neo4jRepository<AreaTematica, String> {
    
    

}
