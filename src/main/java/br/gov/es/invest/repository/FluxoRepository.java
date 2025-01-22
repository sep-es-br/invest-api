package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Fluxo;

public interface FluxoRepository extends Neo4jRepository<Fluxo, String> {
    
    
}