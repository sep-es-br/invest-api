package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.projection.PlanoOrcamentarioDTOProjection;
import br.gov.es.invest.model.PlanoOrcamentario;

public interface PlanoOrcamentarioRepository extends Neo4jRepository<PlanoOrcamentario, String> {
    
    @Query("MATCH (plano:PlanoOrcamentario) RETURN plano ORDER BY plano.codigo")
    public List<PlanoOrcamentario> getAllSimples();

}
