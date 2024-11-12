package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.PlanoOrcamentario;

public interface PlanoOrcamentarioRepository extends Neo4jRepository<PlanoOrcamentario, String> {
    
}
