package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.FonteOrcamentaria;

public interface FonteOrcamentariaRepository extends Neo4jRepository<FonteOrcamentaria, String>{
    
    @Query("MATCH (fonte:FonteOrcamentaria) WHERE fonte.codigo = $codigo RETURN fonte")
    public FonteOrcamentaria findByCodigo(Integer codigo);

}
