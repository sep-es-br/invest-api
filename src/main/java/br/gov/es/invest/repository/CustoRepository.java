package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Custo;

public interface CustoRepository extends Neo4jRepository<Custo, String> {
    
    @Query("MATCH (c:Custo) WHERE (c.anoExercicio = $exercicio) RETURN c\r\n")
    public List<Custo> findByExercicio(String exercicio);
}
