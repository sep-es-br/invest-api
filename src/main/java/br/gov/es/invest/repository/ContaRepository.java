package br.gov.es.invest.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Conta;

public interface ContaRepository extends Neo4jRepository<Conta, String> {
    
    @Query("MATCH (conta:Conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\n" +
            "WHERE NOT EXISTS((conta)<-[:ORIENTA]-(:PlanoOrcamentario))\n" +
            "    AND unidade.codigo = $codUnidade\n" +
            "RETURN conta ")
    public Conta getGenericoByCodUnidade(String codUnidade);

}
