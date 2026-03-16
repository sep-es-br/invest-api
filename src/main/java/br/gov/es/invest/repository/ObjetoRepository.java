package br.gov.es.invest.repository;

import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ObjetoRepository extends Neo4jRepository<Objeto, Long> {
    

    @Query("""
            CALL () {
                MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonteCusto:FonteOrcamentaria),
                (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta), (obj)-[emStatus:EM]->(status:Status)
                OPTIONAL MATCH (conta)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario) 
                OPTIONAL MATCH (fonteExec:FonteOrcamentaria)<-[vinculada:VINCULADA_POR]-(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta)
                OPTIONAL MATCH (obj)-[emEtapa:EM]->(etapa:Etapa)
                ORDER BY unidade.codigo, plano.codigo
                RETURN obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa,
                    re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec
            } WITH  obj, rc, orienta, plano, ri, unidade, rd, execucao, emEtapa, etapa, 
                    re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec
                    re, custo, conta, emStatus, status, indicada, fonteCusto, vinculada, fonteExec
            WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))
                AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio OR execucao.anoExercicio = $exercicio)
                AND ($idUnidade IS NULL OR id(unidade) IN $idUnidade)
                AND ($statusId IS NULL OR id(status) = $statusId)
                AND (
                    $idPo IS NULL
                    OR (\"S.PO\" IN $idPo AND plano IS NULL)
                    OR ( NOT \"S.PO\" IN $idPo AND id(plano) IN toInteger($idPo))
                    )
            RETURN distinct obj, collect(rc), collect(emStatus), collect (status), collect(indicada),
                collect(conta), collect(orienta), collect(plano), collect(ri), collect(unidade),
                collect(rd), collect(execucao), collect(re), collect(custo), collect(vinculada),
                collect(fonteCusto), collect(fonteExec), collect(emEtapa), collect(etapa) 
            SKIP $skip LIMIT $limit
           """)
    public List<ObjetoTiraProjection> getAllListByFilter(Integer exercicio, String nome, List<Long> idUnidade, List<String> idPo, Long statusId, Pageable pageable);

    @Query("""
            MATCH (n:Objeto)-[:EM]->(status:Status) 
            RETURN distinct status 
            ORDER BY status.nome
            """)
    public List<Status> findStatusCadastrados();


    @Query("""
           MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) 
           WHERE id(custo) = $custoId 
           RETURN obj
           """)
    public Objeto getByCusto(Long custoId);

    @Query("""
           MATCH (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:Em]->(anoCusto:Ano),
               (ano:Ano)<-[:EM]-(execucao)
           WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))
               AND ($execicio IS NULL OR ano.ano = $execicio OR anoCusto.ano = $execicio)
               AND ($unidadeId IS NULL OR id(unidade) = $unidadeId)
           RETURN count(distinct obj)
           """)
    public int ammountByFilter(String execicio, String nome, Long unidadeId, String status);

    @Query("""
           MATCH 
               (ano:Ano)<-[:EM]-(exec:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta:Investimento)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[:EM]->(anoCusto:Ano), 
               (unidade)-[ri:IMPLEMENTA]->(conta)<-[ro:ORIENTA]-(plano:PlanoOrcamentario)
           WHERE ($exercicio IS null OR ano.ano = $exercicio OR anoCusto.ano = $exercicio)   
              AND ($nome IS NULL OR apoc.text.clean(conta.nome) contains apoc.text.clean($nome) OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))
              AND ($codPO IS NULL OR id(plano) IN $codPO) 
              AND ($codUnidade IS NULL OR id(unidade) IN $codUnidade)
           RETURN count(distinct obj)
           """)
    public int countByInvestimentoFilter(String nome, List<Long> codUnidade, List<Long> codPO, Integer exercicio);

    
    @Query("""
           CALL () {
               MATCH (conta:Conta)<-[rc:CUSTEADO]-(obj:Objeto)<-[re:ESTIMADO]-(custo:Custo)-[indicada:INDICADA_POR]->(fonte:FonteOrcamentaria),
                   (unidade:UnidadeOrcamentaria)-[ri:IMPLEMENTA]->(conta)
               OPTIONAL MATCH (conta)<-[orienta:ORIENTA]-(plano:PlanoOrcamentario)
               OPTIONAL MATCH (execucao:ExecucaoOrcamentaria)-[rd:DELIMITA]->(conta)
               ORDER BY unidade.codigo, plano.codigo
               RETURN obj, plano, unidade, execucao, custo, conta
           } WITH  obj, plano, unidade, execucao, custo, conta
           WHERE ($nome IS NULL OR apoc.text.clean(obj.nome) CONTAINS apoc.text.clean($nome))
               AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio OR execucao.anoExercicio = $exercicio)
               AND ($unidadeId IS NULL OR id(unidade) = $unidadeId)
               AND ($status IS NULL OR obj.status = $status)
               AND (
                   $planoId IS NULL
                   OR ($planoId = \"S.PO\" AND plano IS NULL)
                   OR ($planoId <> \"S.PO\" AND toInteger($planoId) = id(plano))
                   )
           RETURN count(distinct obj)
           """)
    public int countByFilter(String nome, Long unidadeId, String planoId, String status, Integer exercicio);


    @Query("""
           MATCH (obj:Objeto)<-[:ESTIMADO]-(custo:Custo) 
           WHERE id(obj) = $objetoId 
           DETACH DELETE obj, custo
           """)
    public void removerObjeto(Long objetoId);


    @Query("""
           MATCH (n:TipoPlano)<-[do_tipo:DO_TIPO]-(:Objeto)
           WHERE id(n) IN $ids
           DELETE do_tipo
           """)
    public void removerTipos(List<Long> ids);

    @Query("""
           MATCH (o:Objeto)
           WHERE id(o) = $objetoId
           MATCH (usuario:Agente)
           WHERE id(usuario) = $userId
           CREATE (o)-[r:REVISADO_POR]->(usuario)
           SET r.timestamp = $timestamp
           """)
    public void addRevisor(Long objetoId, Long userId, ZonedDateTime timestamp);


    @Query("""
           MATCH (o:Objeto)
           WHERE id(o) = $objetoId
           MATCH (usuario:Agente)
           WHERE id(usuario) = $userId
           CREATE (o)-[r:ALTERADO_POR]->(usuario)
           SET r.timestamp = $timestamp
           """)
    public void addAlterador(Long objetoId, Long userId, ZonedDateTime timestamp);

    @Query("""
           MATCH (o:Objeto)
           WHERE id(o) = $objetoId
           OPTIONAL MATCH (o)-[oldR:EM]-(:Status)
           DELETE oldR              
           WITH o               
           MATCH (status:Status)
           WHERE id(status) = $statusId
           MERGE (o)-[r:EM]->(status)
           SET r.timestamp = $timestamp
           """)
    public void alterarStatus(Long objetoId, Long statusId, ZonedDateTime timestamp);


    @Query("""
            MATCH (o:Objeto)
            WHERE id(o) = $objetoId        
            MATCH (etapa:Etapa)
            WHERE id(etapa) = $etapaId
            MERGE (o)-[r:EM]->(etapa)
            SET 
               r.timestamp = $timestamp,
               r.devolvido = $devolvido,
               r.atividade = $atividade
            """)
    public void addEtapa(Long objetoId, Long etapaId, Boolean devolvido, String atividade, ZonedDateTime timestamp);


    @Query("""
           MATCH (obj:Objeto)-[emEtapa:EM]->(:Etapa)
           WHERE id(obj) = $objetoId
           ORDER BY emEtapa.timestamp DESC
           LIMIT 1
           WITH emEtapa
           SET 
               emEtapa.avaliadoEm = $avaliadoEm,
               emEtapa.avaliadoPorId = $avaliadoPorId
           """)
    public void updateUltimaEtapa(Long objetoId, ZonedDateTime avaliadoEm, Long avaliadoPorId);
}