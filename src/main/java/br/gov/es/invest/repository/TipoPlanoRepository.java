package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.invest.model.TipoPlano;

public interface TipoPlanoRepository extends Neo4jRepository<TipoPlano, String>{

}
