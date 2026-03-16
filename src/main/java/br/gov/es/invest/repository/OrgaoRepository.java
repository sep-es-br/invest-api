package br.gov.es.invest.repository;

import br.gov.es.invest.model.Orgao;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface OrgaoRepository extends Neo4jRepository<Orgao, Long>{

    @Query("""
           MATCH (orgao:Orgao) 
           WHERE orgao.guid = $guid 
           RETURN orgao
           """)
    public Optional<Orgao> findByGuid(String guid);
    
    @Query("""
           MATCH (orgao:Orgao)
           WHERE (NOT orgao.guid IS NULL AND orgao.guid = $guid)
               OR (orgao.guid IS NULL AND orgao.sigla = $sigla)
           RETURN orgao
           
           """)
    public Optional<Orgao> findByGuidOrSigla(String guid, String sigla);
}