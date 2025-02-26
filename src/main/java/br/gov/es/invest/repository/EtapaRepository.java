package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Etapa;

public interface EtapaRepository extends Neo4jRepository<Etapa, String> {
    
}
