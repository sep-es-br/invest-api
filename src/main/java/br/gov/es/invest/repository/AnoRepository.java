package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.Ano;

public interface AnoRepository extends Neo4jRepository<Ano, String> {
    
    

}
