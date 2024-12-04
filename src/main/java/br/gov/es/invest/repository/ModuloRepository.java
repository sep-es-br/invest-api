package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Modulo;

public interface ModuloRepository extends Neo4jRepository<Modulo, String> {
    
    @Query("MATCH (m:Modulo)<-[r:FILHO_DE*]-(n:Modulo) RETURN m, collect(r), collect(n)")
    public List<Modulo> findAll();


}
