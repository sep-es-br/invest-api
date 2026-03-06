package br.gov.es.invest.repository;

import br.gov.es.invest.model.PlanoOrcamentario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface PlanoOrcamentarioRepository extends Neo4jRepository<PlanoOrcamentario, Long> {
    
    @Query("""
           MATCH (plano:PlanoOrcamentario) 
           OPTIONAL MATCH (plano)-[:ORIENTA]->(conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria), (conta)<-[:CUSTEADO]-(obj)
           WITH plano, unidade
           WHERE CASE
                   WHEN $codsUnidade IS NULL THEN TRUE
                   ELSE unidade.codigo IN $codsUnidade 
                 END        
           RETURN DISTINCT plano ORDER BY plano.codigo
            """)
    
    public List<PlanoOrcamentario> getAllSimples(List<String> codsUnidade);
    
    @Query("MATCH (plano:PlanoOrcamentario)\r\n" + //
            "WHERE id(plano) = $idPlano\r\n" + //
            "RETURN toString(plano.codigo)")
    public String getCodById(Long idPlano);
    
    public Optional<PlanoOrcamentario> findByCodigo(String codigo);
}
