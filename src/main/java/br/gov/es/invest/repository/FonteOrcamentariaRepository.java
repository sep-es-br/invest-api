package br.gov.es.invest.repository;

import br.gov.es.invest.model.FonteOrcamentaria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface FonteOrcamentariaRepository extends Neo4jRepository<FonteOrcamentaria, Long>{
    
    @Query("""
           MATCH (n:FonteOrcamentaria)
           WHERE toInteger(n.codigo) < 1000
           RETURN n
           """)
    public List<FonteOrcamentaria> findFontesExtra();
    
    public Optional<FonteOrcamentaria> findByCodigo(String codigo);

}
