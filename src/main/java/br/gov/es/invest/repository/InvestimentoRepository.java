package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Investimento;

public interface InvestimentoRepository extends  Neo4jRepository<Investimento, String> {

    @Query("MATCH " + 
                "    (conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)<-[ri:INFORMA]-(unidade:UnidadeOrcamentaria), " + 
                "    (conta)<-[rd:DELIMITA]-(exec:ExecucaoOrcamentaria)<-[ro:ORIENTA]-(plano:PlanoOrcamentario) " + 
                "WHERE ($exercicio IS null OR exec.anoExercicio = $exercicio OR custo.anoExercicio = $exercicio) " + 
                "    AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome)) " + 
                "    AND ($codPO IS NULL OR elementId(plano) = $codPO) " + 
                "    AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade) " + 
                "RETURN conta, collect(rc), collect(obj), collect(re), collect(custo), collect(ri), collect(unidade), " + 
                "    collect(rd), collect(exec), collect(ro), collect(plano) SKIP $page LIMIT $pgSize")
    public List<Investimento> findAllByFilter(
        String nome, String codUnidade, String codPO,
        String exercicio, int page, int pgSize
    );

    @Query("MATCH  " +
            "(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)<-[ri:INFORMA]-(unidade:UnidadeOrcamentaria), " +
            "(conta)<-[rd:DELIMITA]-(exec:ExecucaoOrcamentaria)<-[ro:ORIENTA]-(plano:PlanoOrcamentario) " +
            " WHERE ($exercicio IS null OR exec.anoExercicio = $exercicio OR custo.anoExercicio = $exercicio) " + 
            "   AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))" + 
            "AND ($codPO IS NULL OR elementId(plano) = $codPO) " +
            "AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)" +  
            "RETURN count(distinct conta)")
    public int countByFilter(String nome, String codUnidade, String codPO, String exercicio);

    
    
} 
