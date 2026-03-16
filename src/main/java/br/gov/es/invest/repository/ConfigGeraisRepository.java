/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.gov.es.invest.repository;

import br.gov.es.invest.model.ConfigGerais;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

/**
 *
 * @author gean.carneiro
 */
public interface ConfigGeraisRepository extends Neo4jRepository<ConfigGerais, Long> {
    
    @Query("""
            MERGE (c:ConfigGerais)
            ON CREATE SET c.createdAt = datetime()
            RETURN c
          """)
    ConfigGerais getOrCreate();
    
    @Query("""
           MATCH (c:ConfigGerais) 
           RETURN c 
           LIMIT 1
           """)
    Optional<ConfigGerais> getConfig();
    
}
