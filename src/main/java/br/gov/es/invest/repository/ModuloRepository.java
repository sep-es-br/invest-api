package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Modulo;

public interface ModuloRepository extends Neo4jRepository<Modulo, String> {
    
    @Query("MATCH (m:Modulo)<-[r:FILHO_DE*]-(n:Modulo) \r\n" + //
                "WHERE NOT EXISTS((m)-[:FILHO_DE]->(:Modulo))\r\n" + //
                "RETURN m, collect(r), collect(n)")
    public List<Modulo> findAll();

    @Query("MATCH (modulo:Modulo)\r\n" + //
            "WHERE modulo.pathId = $pathId\r\n" + //
            "OPTIONAL MATCH (modulo)<-[filho_de:FILHO_DE*]-(filho:Modulo)" + //
            "RETURN modulo, collect(filho_de), collect(filho)")
    public Modulo findByPathId(String pathId);

    @Query("MATCH (modulo:Modulo)<-[filho_de:FILHO_DE*]-(filho:Modulo)<-[:PODE]-(grupo:Grupo)\r\n" + //
            "WHERE elementId(modulo) = $moduloId\r\n" + //
            "    AND elementId(grupo) = $grupoId \r\n" + //
            "RETURN count(modulo)>0")
    public boolean temAcessoPorDescedencia(String grupoId, String moduloId);
}
