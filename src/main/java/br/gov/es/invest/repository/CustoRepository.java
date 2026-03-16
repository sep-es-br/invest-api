package br.gov.es.invest.repository;

import br.gov.es.invest.model.Custo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CustoRepository extends Neo4jRepository<Custo, Long> {
    
    @Query("""
           MATCH (custo:Custo)
           RETURN DISTINCT custo.anoExercicio
           """)
    public Set<Integer> getAnosExercicio();
    
    @Query("""
           MATCH (custo:Custo)-[:ESTIMADO]->(obj:Objeto)
           WHERE custo.anoExercicio = $anoExercicio AND id(obj) = $idObjeto
           RETURN id(custo)
           """)
    public Optional<Long> findIdByAnoExercicioObjetoId(Integer anoExercicio, Long idObjeto);
    
    @Query("""
           WITH $valores AS valores
           
           MATCH (c:Custo)
           WHERE id(c) = $custoId
           
           OPTIONAL MATCH (c)-[old:INDICADA_POR]->(f:FonteOrcamentaria)
           WHERE NOT f.codigo IN [v IN valores | v.fonte]
           DELETE old
           
           WITH c, valores
           
           UNWIND valores AS valor
           MATCH (fonte:FonteOrcamentaria {codigo: valor.fonte})
           
           MERGE (c)-[r:INDICADA_POR]->(fonte)
           SET r.planejado = valor.planejado,
               r.contratado = valor.contratado
           """)
    public void updateCustos(Long custoId, List<Map<String, Object>> valores);
    
}
