package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Conta;

public interface ContaRepository extends Neo4jRepository<Conta, Long> {
    
    @Query("""
           MATCH (conta:Conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)
           WHERE NOT EXISTS((conta)<-[:ORIENTA]-(:PlanoOrcamentario))
               AND unidade.codigo = $codUnidade
           RETURN conta 
           """)
    public Conta getGenericoByCodUnidade(String codUnidade);

    @Query("""
           MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(:Status{statusId: 'CADASTRADO'}
           WHERE id(conta) IN $ids 
           RETURN DISTINCT conta
           """)
    public List<Conta> filtrarContasForaProcessamento(List<Long> ids);

}
