package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.ExecucaoOrcamentaria;

public interface ExecucaoOrcamentariaRepository extends Neo4jRepository<ExecucaoOrcamentaria, String> {
    

    @Query("MATCH (execucao:ExecucaoOrcamentaria)-[:EM]->(ano:Ano)\r\n" + //
                "WHERE ano.ano = $exercicio\r\n" + //
                "RETURN SUM(execucao.orcamento)")
    public Double getTotalOrcadoByAno(String exercicio);

}
