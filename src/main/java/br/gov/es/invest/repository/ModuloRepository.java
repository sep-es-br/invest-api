package br.gov.es.invest.repository;

import br.gov.es.invest.model.Modulo;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ModuloRepository extends Neo4jRepository<Modulo, Long> {
    
    @Query("""
           MATCH (m:Modulo)
           WHERE NOT EXISTS((m)-[:FILHO_DE]->(:Modulo))
           OPTIONAL MATCH (m)<-[r:FILHO_DE*]-(n:Modulo)
           RETURN m, collect(r), collect(n)
           """)
    @Override
    public List<Modulo> findAll();

    public Modulo findByPathId(String pathId);

    @Query("""
           MATCH (modulo:Modulo)<-[filho_de:FILHO_DE*]-(filho:Modulo)<-[:PODE]-(grupo:Grupo)
           WHERE id(modulo) = $moduloId
               AND id(grupo) = $grupoId 
           RETURN count(modulo)>0
           """)
    public boolean temAcessoPorDescedencia(Long grupoId, Long moduloId);
}
