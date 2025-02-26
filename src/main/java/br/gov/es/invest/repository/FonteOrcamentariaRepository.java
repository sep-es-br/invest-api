package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.FonteOrcamentaria;

public interface FonteOrcamentariaRepository extends Neo4jRepository<FonteOrcamentaria, String>{
    
    @Query("MATCH (fonte:FonteOrcamentaria) WHERE fonte.codigo = $codigo RETURN fonte")
    public FonteOrcamentaria findByCodigo(String codigo);

    @Query("MATCH (n:FonteOrcamentaria) \r\n" + //
            "WHERE toInteger(n.codigo) < 1000\r\n" + //
            "RETURN n")
    public List<FonteOrcamentaria> findFontesExtra();

}
