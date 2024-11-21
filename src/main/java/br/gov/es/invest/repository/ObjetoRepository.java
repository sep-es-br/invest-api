package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Objeto;

public interface ObjetoRepository extends Neo4jRepository<Objeto, String> {
    

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)\r\n" + //
                "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                "    AND ($execicio IS NULL OR custo.anoExercicio = $execicio)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
                "RETURN obj, collect(re), collect(rd), collect(execucao), collect(custo), collect(ri), collect(unidade), collect(rc), collect(conta)")
    public List<Objeto> getAllByFilter(String execicio, String nome, String unidadeId, String status); 


    @Query("MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) WHERE elementId(custo) = $custoId RETURN obj")
    public Objeto getByCusto(String custoId);

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)\r\n" + //
                "  WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                "    AND ($execicio IS NULL OR custo.anoExercicio = $execicio)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
                "  RETURN count(distinct obj)")
    public int ammountByFilter(String execicio, String nome, String unidadeId, String status);

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo), \r\n" + 
            "(execucao)<-[ro:ORIENTA]-(plano:PlanoOrcamentario) " +
            " WHERE ($exercicio IS null OR execucao.anoExercicio = $exercicio OR custo.anoExercicio = $exercicio) " + 
            "   AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))" + 
            "AND ($codPO IS NULL OR elementId(plano) = $codPO) " +
            "AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)" +  
            "RETURN count(distinct obj)")
    public int countByInvestimentoFilter(String nome, String codUnidade, String codPO, String exercicio);
}
