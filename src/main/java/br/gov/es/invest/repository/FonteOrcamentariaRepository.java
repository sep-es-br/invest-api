package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.FonteOrcamentaria;

public interface FonteOrcamentariaRepository extends Neo4jRepository<FonteOrcamentaria, String>{
    
}
