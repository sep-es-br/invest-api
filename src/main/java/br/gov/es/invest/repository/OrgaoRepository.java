package br.gov.es.invest.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Orgao;

public interface OrgaoRepository extends Neo4jRepository<Orgao, String>{

    @Query("MATCH (orgao:Orgao) WHERE orgao.guid = $guid RETURN orgao")
    public Optional<Orgao> findByGuid(String guid);
    
}