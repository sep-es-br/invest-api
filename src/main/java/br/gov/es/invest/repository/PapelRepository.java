package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Papel;

public interface PapelRepository extends Neo4jRepository<Papel, String> {
    
}
