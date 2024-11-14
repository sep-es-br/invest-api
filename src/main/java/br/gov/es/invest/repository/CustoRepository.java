package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.ValoresTotaisCusto;
import br.gov.es.invest.model.Custo;

public interface CustoRepository extends Neo4jRepository<Custo, String> {
    
    @Query("MATCH (c:Custo) WHERE (c.anoExercicio = $exercicio) RETURN c LIMIT 50\r\n")
    public List<Custo> findByExercicio(String exercicio);

    @Query("MATCH (custo:Custo) RETURN DISTINCT custo.anoExercicio AS ano")
    public List<String> findAllAnos();

    @Query("MATCH (custo:Custo) " +
            "WHERE custo.anoExercicio = $exercicio " +
            "RETURN SUM(custo.previsto) AS previsto, SUM(custo.contratado) AS contratado")
    public List<ValoresTotaisCusto> getTotais(String exercicio);


    @Query("MATCH (custo:Custo) " +
            "WHERE custo.anoExercicio = $exercicio " +
            "RETURN SUM(custo.previsto) AS previsto")
    public Double getTotaisPrevisto(String exercicio);

    @Query("MATCH (custo:Custo) " +
            "WHERE custo.anoExercicio = $exercicio " +
            "RETURN SUM(custo.contratado) AS contratado")
    public Double getTotaisContratado(String exercicio);
}
