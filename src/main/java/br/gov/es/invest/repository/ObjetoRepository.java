package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;

public interface ObjetoRepository extends Neo4jRepository<Objeto, String> {
    

    @Query("CALL () {\r\n" + //
                        "    MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonteCusto:FonteOrcamentaria),\r\n" + //
                        "    (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta), (obj)-[emStatus:EM]->(status:Status)\r\n" + //
                        "    OPTIONAL MATCH (conta)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                        "    OPTIONAL MATCH (fonteExec:FonteOrcamentaria)<-[vinculada:VINCULADA_POR]-(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta)\r\n" + //
                        "    OPTIONAL MATCH (obj)-[emEtapa:EM]->(etapa:Etapa)\r\n" + //
                        "    ORDER BY unidade.codigo, plano.codigo\r\n" + //
                        "    RETURN obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa,\r\n" + //
                        "        re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec\r\n" + //
                        "} WITH  obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa, \r\n" + //
                        "        re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec\r\n" + //
                        "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                        "    AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio OR execucao.anoExercicio = $exercicio)\r\n" + //
                        "    AND ($idUnidade IS NULL OR elementId(unidade) = $idUnidade)\r\n" + //
                        "    AND ($statusId IS NULL OR elementId(status) = $statusId)\r\n" + //
                        "    AND (\r\n" + //
                        "        $idPo IS NULL\r\n" + //
                        "        OR ($idPo = \"S.PO\" AND plano IS NULL)\r\n" + //
                        "        OR ($idPo <> \"S.PO\" AND $idPo = elementId(plano))\r\n" + //
                        "        )\r\n" + //
                        "RETURN distinct obj, collect(rc), collect(emStatus), collect (status), collect(indicada),\r\n" + //
                        "    collect(conta), collect(orienta), collect(plano), collect(ri), collect(unidade),\r\n" + //
                        "    collect(rd), collect(execucao), collect(re), collect(custo), collect(vinculada),\r\n" + //
                        "    collect(fonteCusto), collect(fonteExec), collect(emEtapa), collect(etapa)" + //
                "SKIP $skip LIMIT $limit")
    public List<ObjetoTiraProjection> getAllListByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String statusId, Pageable pageable);

    @Query("CALL () {\r\n" + //
                        "    MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonteCusto:FonteOrcamentaria),\r\n" + //
                        "    (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta), (obj)-[emStatus:EM]->(status:Status)\r\n" + //
                        "    OPTIONAL MATCH (conta)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                        "    OPTIONAL MATCH (fonteExec:FonteOrcamentaria)<-[vinculada:VINCULADA_POR]-(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta)\r\n" + //
                        "    OPTIONAL MATCH (obj)-[emEtapa:EM]->(etapa:Etapa)\r\n" + //
                        "    ORDER BY unidade.codigo, plano.codigo\r\n" + //
                        "    RETURN obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa,\r\n" + //
                        "        re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec\r\n" + //
                        "} WITH  obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa, \r\n" + //
                        "        re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec\r\n" + // //
                        "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                        "    AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio OR execucao.anoExercicio = $exercicio)\r\n" + //
                        "    AND ($idUnidade IS NULL OR elementId(unidade) = $idUnidade)\r\n" + //
                        "    AND ($statusId IS NULL OR elementId(status) = $statusId)\r\n" + //
                        "    AND (\r\n" + //
                        "        $idPo IS NULL\r\n" + //
                        "        OR ($idPo = \"S.PO\" AND plano IS NULL)\r\n" + //
                        "        OR ($idPo <> \"S.PO\" AND $idPo = elementId(plano))\r\n" + //
                        "        )\r\n" + //
                        "RETURN distinct obj, collect(rc), collect(emStatus), collect (status), collect(indicada),\r\n" + //
                        "    collect(conta), collect(orienta), collect(plano), collect(ri), collect(unidade),\r\n" + //
                        "    collect(rd), collect(execucao), collect(re), collect(custo), collect(vinculada),\r\n" + //
                        "    collect(fonteCusto), collect(fonteExec), collect(emEtapa), collect(etapa)")
        public List<ObjetoTiraProjection> getAllListByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String statusId);

        @Query("MATCH (n:Objeto)-[:EM]->(status:Status) \r\n" + //
                "RETURN distinct status \r\n" + //
                "ORDER BY status.nome")
    public List<Status> findStatusCadastrados();


    @Query("MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) WHERE elementId(custo) = $custoId RETURN obj")
    public Objeto getByCusto(String custoId);

    @Query("MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:Em]->(anoCusto:Ano),\r\n" + //
            "    (ano:Ano)<-[:EM]-(execucao)" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
            "    AND ($execicio IS NULL OR ano.ano = $execicio OR anoCusto.ano = $execicio)" + //
            "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
            "  RETURN count(distinct obj)")
    public int ammountByFilter(String execicio, String nome, String unidadeId, String status);

    @Query("MATCH \r\n" + //
            "    (ano:Ano)<-[:EM]-(exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano), \r\n" + //
            "    (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
            "WHERE ($exercicio IS null OR ano.ano = $exercicio OR anoCusto.ano = $exercicio)   " + 
            "   AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))" + 
            "AND ($codPO IS NULL OR elementId(plano) = $codPO) " +
            "AND ($codUnidade IS NULL OR elementId(unidade) = $codUnidade)" +  
            "RETURN count(distinct obj)")
    public int countByInvestimentoFilter(String nome, String codUnidade, String codPO, Integer exercicio);

    
    @Query("CALL () {\r\n" + //
                "        MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonte:FonteOrcamentaria),\r\n" + //
                "        (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta)\r\n" + //
                "    OPTIONAL MATCH (conta)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                "    OPTIONAL MATCH (execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta)\r\n" + //
                "    ORDER BY unidade.codigo, plano.codigo\r\n" + //
                "    RETURN obj, plano, unidade, execucao, custo, conta\r\n" + //
                "} WITH  obj, plano, unidade, execucao, custo, conta\r\n" + //
                "WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))\r\n" + //
                "    AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio OR execucao.anoExercicio = $exercicio)\r\n" + //
                "    AND ($unidadeId IS NULL OR elementId(unidade) = $unidadeId)\r\n" + //
                "    AND ($status IS NULL OR obj.status = $status)\r\n" + //
                "    AND (\r\n" + //
                "        $planoId IS NULL\r\n" + //
                "        OR ($planoId = \"S.PO\" AND plano IS NULL)\r\n" + //
                "        OR ($planoId <> \"S.PO\" AND $planoId = elementId(plano))\r\n" + //
                "        )\r\n" + //
                "RETURN count(distinct obj)")
    public int countByFilter(String nome, String unidadeId, String planoId, String status, Integer exercicio);

    @Query("MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) \n" +
                "WHERE elementId(obj) = $objetoId \n" + 
                "DETACH DELETE obj, custo")
        public void removerObjeto(String objetoId);


        @Query("MATCH (n:TipoPlano)<-[do_tipo:DO_TIPO]-(:Objeto)\r\n" + //
                        "WHERE elementId(n) IN $ids\r\n" + //
                        "DELETE do_tipo")
        public void removerTipos(List<String> ids);
}