package br.gov.es.invest.repository;

import br.gov.es.invest.model.Campo;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CampoRepository extends Neo4jRepository<Campo, Long> {
    
}
