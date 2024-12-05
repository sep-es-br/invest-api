package br.gov.es.invest.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Pode;

public interface PodeRepository extends Neo4jRepository<Pode, String> {
    
    @Query("MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)\r\n" + //
            "WHERE elementId(modulo) = $moduloId\r\n" + //
            "    AND elementId(grupo) = $grupoId\r\n" + //
            "RETURN pode")
    public Optional<Pode> findByGrupoModulo(String moduloId, String grupoId);

}
