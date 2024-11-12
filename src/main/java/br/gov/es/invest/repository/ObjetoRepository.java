package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Objeto;

public interface ObjetoRepository extends Neo4jRepository<Objeto, String> {
    

    @Query("MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)<-[ri:INFORMA]-(unidade:UnidadeOrcamentaria)\r\n" + //
                "  WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                "    AND ($execicio IS NULL OR custo.anoExercicio = $execicio)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
                "  RETURN obj, collect(re), collect(custo), collect(ri), collect(unidade), collect(rc), collect(conta)")
    public List<Objeto> getAllByFilter(String execicio, String nome, String unidadeId, String status); 


    @Query("MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) WHERE elementId(custo) = $custoId RETURN obj")
    public Objeto getByCusto(String custoId);
}
