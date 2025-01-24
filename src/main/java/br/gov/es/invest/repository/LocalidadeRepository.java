package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Localidade;

public interface LocalidadeRepository extends Neo4jRepository<Localidade, String> {
    
}
