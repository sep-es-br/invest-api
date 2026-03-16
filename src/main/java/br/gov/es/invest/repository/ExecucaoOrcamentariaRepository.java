package br.gov.es.invest.repository;

import br.gov.es.invest.model.ExecucaoOrcamentaria;
import java.util.Set;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ExecucaoOrcamentariaRepository extends Neo4jRepository<ExecucaoOrcamentaria, Long> {
    

    @Query("""
           MATCH (execucao:ExecucaoOrcamentaria)
           RETURN DISTINCT execucao.anoExercicio
           """)
    public Set<Integer> getAnosExercicio();

    @Query("""
           MATCH (exec:ExecucaoOrcamentaria)-[vinculada:VINCULADA_POR]->(fonte:FonteOrcamentaria)
           WHERE exec.anoExercicio = $ano
           SET vinculada.novo = $novo
           """)
    public void setaTudoNovo(Integer ano, boolean novo);

}
