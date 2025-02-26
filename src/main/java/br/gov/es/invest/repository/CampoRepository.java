package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Campo;

public interface CampoRepository extends Neo4jRepository<Campo, String> {
    
}
