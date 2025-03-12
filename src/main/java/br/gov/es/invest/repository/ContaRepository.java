package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Conta;

public interface ContaRepository extends Neo4jRepository<Conta, String> {
    
    @Query("MATCH (conta:Conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\n" +
            "WHERE NOT EXISTS((conta)<-[:ORIENTA]-(:PlanoOrcamentario))\n" +
            "    AND unidade.codigo = $codUnidade\n" +
            "RETURN conta ")
    public Conta getGenericoByCodUnidade(String codUnidade);

        @Query("MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)\r\n" + //
                "WHERE elementId(conta) IN $ids \r\n" + //
                "    AND NOT EXISTS((obj)-[:EM]->(:Etapa))\r\n" + //
                "RETURN conta")
        public List<Conta> filtrarContasForaProcessamento(List<String> ids);

}
