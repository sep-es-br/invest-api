package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Ano;

public interface AnoRepository extends Neo4jRepository<Ano, String> {
    
    @Query("MATCH (ano:Ano) WHERE ano.ano = $ano RETURN ano")
    public Ano getByAno(String ano);

}
