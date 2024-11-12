package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.UnidadeOrcamentaria;

public interface UnidadeOrcamentariaRepository extends Neo4jRepository<UnidadeOrcamentaria, String> {
    
}
