package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.IValoresCusto;
import br.gov.es.invest.dto.ValoresCusto;
import br.gov.es.invest.model.Custo;

public interface CustoRepository extends Neo4jRepository<Custo, String> {
    
    @Query("MATCH (c:Custo)-[:EM]->(ano:Ano) WHERE (ano.ano = $exercicio) RETURN c\r\n")
    public List<Custo> findByExercicio(String exercicio);

    @Query("MATCH (custo:Custo)-[:EM]->(ano:Ano) " +
            "WHERE ano.ano = $exercicio " +
            "RETURN custo")
    public List<IValoresCusto> getTotais(String exercicio);

}
