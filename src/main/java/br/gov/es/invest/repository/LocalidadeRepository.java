package br.gov.es.invest.repository;

import br.gov.es.invest.model.Localidade;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LocalidadeRepository extends Neo4jRepository<Localidade, Long> {
    
}
