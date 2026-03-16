package br.gov.es.invest.repository;

import br.gov.es.invest.model.Pode;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface PodeRepository extends Neo4jRepository<Pode, Long> {
    
    @Query("""
           MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)
           WHERE id(modulo) = $moduloId
               AND id(grupo) = $grupoId
           RETURN pode
           """)
    public Optional<Pode> findByGrupoModulo(Long moduloId, Long grupoId);

}
