package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.ExecucaoOrcamentaria;

public interface ExecucaoOrcamentariaRepository extends Neo4jRepository<ExecucaoOrcamentaria, String> {
    
}
