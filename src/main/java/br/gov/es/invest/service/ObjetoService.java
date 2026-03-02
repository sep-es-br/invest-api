package br.gov.es.invest.service;

import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.dto.OrdemItemDto;
import br.gov.es.invest.dto.projection.TiraObjetoProjection;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.EtapaEnum;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ObjetoRepository;
import br.gov.es.invest.utils.DataListResult;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjetoService {
    
    private final ObjetoRepository repository;

    private final Neo4jOperations neo4jOperations;
    private final Neo4jClient neo4jClient;

    
    private final InvestimentoService investimentoService;
    private final UnidadeOrcamentariaService unidadeService;
    private final PlanoOrcamentarioService planoService;
    private final ContaService contaService;
    
    private final StatusService statusService;
    private final EtapaService etapaSrv;

    private final FluxoService fluxoService;
    
    private final TipoPlanoService tpPlanoSrv;



    public void saveAll(List<Objeto> objetos) {
        repository.saveAll(objetos);
    }

    public Objeto save(Objeto objeto) {
        

        if(objeto.getEmStatus() == null) {
            
            ZonedDateTime agora = ZonedDateTime.now();
            
            Status novoStatus = statusService.getByStatusId(StatusEnum.SOLICITADO.name()).get();

            EmStatus emStatus = new EmStatus();

            emStatus.setStatus(novoStatus);
            emStatus.setTimestamp(agora);

            objeto.setEmStatus(emStatus);

            Fluxo fluxo = fluxoService.findByFluxoId("avaliacaoPip");

            EmEtapa emEtapa = new EmEtapa();
            emEtapa.setAtividade("Avaliar Solicitação");
            emEtapa.setDevolvido(false);
            emEtapa.setEtapa(fluxo.getEtapaInicial());
            emEtapa.setTimestamp(agora);
            
            objeto.getEmEtapa().add(emEtapa);
            
        }

        return repository.save(objeto);
    }

    public Objeto findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public List<String> listarHashUsadosPorDemandaPublica() {
        
        String cypher = 
        """
            MATCH (obj:Objeto)-[:DO_TIPO]->(n:TipoPlano) 
            WHERE n.sigla = 'DA'
              AND obj.hashProposta IS NOT NULL
            RETURN obj.hashProposta AS hash
        """;

        return new ArrayList<>(neo4jClient
                    .query(cypher)
                    .fetchAs(String.class)
                    .mappedBy((typeSystem, record) -> record.get("hash").asString())
                    .all()
        );
        
    }

    public DataListResult<ObjetoTiraDTO> getAllListByFilter(Boolean audiencia, Integer exercicio, Integer gnd, String nome, List<Long> idUnidade, List<Long> idPo, Long statusId, Long fonteId, List<OrdemItemDto> ordem, Pageable pageable){
        
        String cypherBase = """
                MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status),
                    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta)
                WHERE
                    ($nome IS NULL OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))
                    AND ($idsUnidade IS NULL OR id(unidade) IN $idsUnidade)
                    AND ($idStatus IS NULL OR id(status) = $idStatus)
                    AND ($gnd IS NULL OR obj.gnd = $gnd)
                    AND CASE
                            WHEN $audiencia THEN obj.hashProposta IS NOT NULL
                            ELSE true
                        END

                OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)
                WHERE $idsPo IS NULL OR id(plano) IN $idsPo


                // Filtro decisivo para PO
                WITH conta, obj, status, unidade, plano
                WHERE $idsPo IS NULL OR NOT plano IS NULL

                CALL(conta){
                    MATCH (conta)
                    OPTIONAL MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vp:VINCULADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE
                        exec.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = id(fonte))
                    RETURN
                        SUM(vp.orcado) AS totalOrcado,
                        SUM(vp.autorizado) AS totalAutorizado,
                        SUM(vp.dispSemReserva) AS totalDisponivel,
                        SUM(REDUCE(total=0,e IN vp.empenhado | total + e ))  AS totalEmpenhado
                }
                CALL(obj) {
                    MATCH (obj)
                    OPTIONAL MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[ip:INDICADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE 
                        custo.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = id(fonte))
                    RETURN
                        sum(ip.planejado) AS totalPlanejado, 
                        sum(ip.contratado) AS totalContratado 
                }

                """;

        Map<String, Object> params = new HashedMap<>();
        params.put("exercicio", exercicio);
        params.put("nome", nome);
        params.put("idsUnidade", idUnidade);
        params.put("idStatus", statusId);
        params.put("idsPo", idPo);
        params.put("idFonte", fonteId);
        params.put("gnd", gnd);
        params.put("audiencia", audiencia);

        String cypherQuery = cypherBase +
                        """
                        RETURN DISTINCT
                            id(obj) AS id,
                            unidade.codigo AS codUnidade,
                            unidade.sigla AS siglaUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            plano.codigo AS codPO,
                            obj.nome AS nome,
                            obj.tipo AS tipo,
                            totalPlanejado,
                            totalContratado,
                            totalOrcado,
                            totalAutorizado,
                            totalEmpenhado,
                            totalDisponivel,
                            status.nome AS status

                        """;

        if(ordem != null && !ordem.isEmpty())
            cypherQuery += "ORDER BY " + ordem.stream().map(item -> item.campo() + " " + item.direcao()).collect(Collectors.joining(", ")) + "\n";
       
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }

        String cypherCount = cypherBase +
                            """
                            RETURN
                                count(DISTINCT obj)
                            """;

        

        return new DataListResult<>(
            neo4jOperations.findAll(cypherQuery, params, ObjetoTiraDTO.class),
            (int) neo4jOperations.count(cypherCount, params)
            );

    }

    public DataListResult<ObjetoTiraDTO> getAllListByFilterEmProcessamento(Integer exercicio, Integer gnd, String nome, List<Long> idUnidade, List<Long> idPo, Long statusId, Long etapaId, Long fonteId, Pageable pageable){
         
        String cypherBase = """
                MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status),
                    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta), (obj)-[:EM]->(etapa:Etapa)
                WHERE
                    ($nome IS NULL OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))
                     AND ($idsUnidade IS NULL OR id(unidade) IN $idsUnidade)
                     AND ($idStatus IS NULL OR id(status) = $idStatus)
                     AND ($idEtapa IS NULL OR id(etapa) = $idEtapa)
                     AND NOT EXISTS ((obj)-[:EM]->(:Status{statusId: 'CADASTRADO'}))
                     AND ($gnd IS NULL OR obj.gnd = $gnd)

                OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)
                WHERE $idsPo IS NULL OR id(plano) IN $idsPo


                // Filtro decisivo para PO
                WITH conta, obj, status, unidade, plano
                WHERE $idsPo IS NULL OR NOT plano IS NULL

                CALL(conta){
                    MATCH (conta)
                    OPTIONAL MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vp:VINCULADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE
                        exec.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = id(fonte))
                    RETURN
                        SUM(vp.orcado) AS totalOrcado,
                        SUM(vp.autorizado) AS totalAutorizado,
                        SUM(vp.dispSemReserva) AS totalDisponivel,
                        SUM(REDUCE(total=0,e IN vp.empenhado | total + e ))  AS totalEmpenhado
                }
                CALL(obj) {
                    MATCH (obj)
                    OPTIONAL MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[ip:INDICADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE 
                        custo.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = id(fonte))
                    RETURN
                        sum(ip.planejado) AS totalPlanejado, 
                        sum(ip.contratado) AS totalContratado 
                }

                """;

        Map<String, Object> params = new HashedMap<>();
        params.put("exercicio", exercicio);
        params.put("nome", nome);
        params.put("idsUnidade", idUnidade);
        params.put("idStatus", statusId);
        params.put("idEtapa", etapaId);
        params.put("idsPo", idPo);
        params.put("idFonte", fonteId);
        params.put("gnd", gnd);

        String cypherQuery = cypherBase +
                        """
                        RETURN DISTINCT
                            id(obj) AS id,
                            unidade.codigo AS codUnidade,
                            unidade.sigla AS siglaUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            plano.codigo AS codPO,
                            obj.nome AS nome,
                            obj.tipo AS tipo,
                            totalPlanejado,
                            totalContratado,
                            totalOrcado,
                            totalAutorizado,
                            totalEmpenhado,
                            totalDisponivel,
                            status.nome AS status
                        ORDER BY codUnidade, codPO
                        
                        """;

        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }

        String cypherCount = cypherBase +
                            """
                            RETURN
                                count(DISTINCT obj)
                            """;

        
        return new DataListResult<>(
            neo4jOperations.findAll(cypherQuery, params, ObjetoTiraDTO.class),
            (int) neo4jOperations.count(cypherCount, params)
        );


    }

    public List<Objeto> getAllByFilter(Integer exercicio, String nome, Long idUnidade, String idPo, Long statusId, Pageable pageable) {
        
        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta(null);
        objetoProbe.setConta(contaProbe);

        if(nome != null) {
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("conta.nome", ExampleMatcher.GenericPropertyMatchers.ignoreCase().contains());
        }

        if(idUnidade != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(idUnidade);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        if(idPo != null) {
            if(idPo.equals("S.PO")) {
                matcher = matcher.withMatcher("conta.planoOrcamentario", ExampleMatcher.GenericPropertyMatchers.exact()).withIncludeNullValues();
            } else {
                PlanoOrcamentario planoProbe = new PlanoOrcamentario();
                planoProbe.setId(Long.valueOf(idPo));
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        // if(statusId != null) {
        //     Status statusProbe = new Status();
        //     statusProbe.setId(statusId);

        //     EmStatus emStatusProbe = new EmStatus();
        //     emStatusProbe.setStatus(statusProbe);

        //     objetoProbe.setEmStatus(emStatusProbe);


        // }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe, matcher));

        if(statusId != null) {
            objetoFiltrado = objetoFiltrado.stream()
                            .filter( obj -> obj.getEmStatus().getStatus().getId().equals(statusId) )
                            .toList();      
        }

        if(pageable != null)
            objetoFiltrado = objetoFiltrado.subList(Math.toIntExact(pageable.getOffset()) , Math.toIntExact(pageable.getOffset()+Long.min(objetoFiltrado.size(), pageable.getPageSize()) ) );

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(exercicio, null);
        }

        return objetoFiltrado;
        
        
    }

    public List<Objeto> findByFilter(
        String nome, Long unidadeId, String planoId,
        Integer anoExercicio, Long fonteId
    ) {

        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta(null);
        objetoProbe.setConta(contaProbe);

        if(nome != null) {
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("conta.nome", ExampleMatcher.GenericPropertyMatchers.ignoreCase().contains());
        }

        if(unidadeId != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(unidadeId);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        if(planoId != null) {
            if(planoId.equals("S.PO")) {
                matcher = matcher.withMatcher("conta.planoOrcamentario", ExampleMatcher.GenericPropertyMatchers.exact()).withIncludeNullValues();
            } else {
                PlanoOrcamentario planoProbe = new PlanoOrcamentario();
                planoProbe.setId(Long.valueOf(planoId));
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe, matcher));

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(anoExercicio, fonteId);
        }

        return objetoFiltrado;
    }

    public void updateStatus(Long objId, Status novoStatus) {
        Optional<Objeto> optObjeto = repository.findById(objId);
        
        if(optObjeto.isEmpty()) return;

        Objeto obj = optObjeto.get();
        obj.getEmStatus().setStatus(novoStatus);
        obj.getEmStatus().setTimestamp(ZonedDateTime.now());

        repository.save(obj);

    }

    public Optional<Objeto> getById(Long id, boolean updateStatus) {
        Optional<Objeto> optObjeto = repository.findById(id);
        
        if(optObjeto.isPresent() 
            && updateStatus
            && optObjeto.get().getEmStatus().getStatus().getStatusId().equals(StatusEnum.SOLICITADO)){
            Status novoStatus = statusService.getByStatusId(StatusEnum.EM_ANALISE.name()).get();
            
            
            statusService.aplicarStatus(optObjeto.get(), novoStatus);
            
            EmEtapa emEtapa = new EmEtapa();
            emEtapa.setAtividade("Avaliar Solicitação");
            emEtapa.setDevolvido(false);
            emEtapa.setEtapa(etapaSrv.getEtapaByEtapaId(EtapaEnum.ANALISE_TECNICA));
            emEtapa.setTimestamp(ZonedDateTime.now());
            
            etapaSrv.addEmEtapa(optObjeto.get(), emEtapa);
            
            optObjeto = repository.findById(id);
        }

        return optObjeto;
    }

    public Optional<Objeto> getById(Long id) {
        return this.getById(id, false);
    }

    public List<Objeto> getAllByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public Objeto removerObjeto(Long objetoId) {
        Optional<Objeto> optObjeto = repository.findById(objetoId);

        if(optObjeto.isEmpty())
            return null;

        repository.removerObjeto(objetoId);
        return optObjeto.get();
    }

    public List<Objeto> findObjetoByConta(Conta conta) {
        return this.findObjetoByConta(conta.getId());
    }

    public List<Objeto> findObjetoByConta(Long contaId) {
        Objeto objetoProbe = new Objeto();
        Conta contaProbe = new Conta(null);
        contaProbe.setId(contaId);
        objetoProbe.setConta(contaProbe);

        return repository.findAll(Example.of(objetoProbe));
    }

    public DataListResult<TiraObjetoProjection> findObjetoCadastradoByContaBy(
            List<Long> idsConta, Integer exercicio, Long idFonte, Integer gnd, Pageable pageable
    ) {
        String cypher = """
                        MATCH (inv:Investimento)
                        WHERE id(inv) IN $idsConta
                        WITH inv
                        MATCH (inv)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status{statusId: 'CADASTRADO'}),\r
                                (po:PlanoOrcamentario)-[:ORIENTA]->(inv)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r
                        """ ;

        HashMap<String, Object> params = new HashMap<>();
        params.put("idsConta", idsConta);
        params.put("exercicio", exercicio);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);

        String cypherCount = cypher + "RETURN COUNT(DISTINCT obj)";

        int count = (int) this.neo4jOperations.count(cypherCount, params);
        
        String cypherQuery = cypher + 
                
                """
                    CALL (obj) {\r
                        MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r
                        WHERE ($idFonte IS NULL OR id(fonteCusto) = $idFonte)\r
                            AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio)\r
                            AND ($gnd IS NULL OR indicada_por.gnd = $gnd)\r
                        RETURN \r
                            ($gnd IS NULL OR indicada_por.gnd = $gnd) AS gnd,\r
                            sum(indicada_por.planejado) AS totalPlanejado,\r
                            sum(indicada_por.contratado) AS totalContratado \r
                    }\r
                    CALL (inv) {\r
                        MATCH (inv)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r
                        WHERE ($idFonte IS NULL OR id(fonteExec) = $idFonte)\r
                            AND ($exercicio IS NULL OR exec.anoExercicio = $exercicio)\r
                            AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)\r
                        RETURN\r
                            sum(vinculada_por.orcado) AS totalOrcado,\r
                            sum(vinculada_por.autorizado) AS totalAutorizado,\r
                            sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS totalEmpenhado,\r
                            sum(vinculada_por.dispSemReserva) AS totalDisponivel\r
                    }\r
                    RETURN DISTINCT 
                       id(inv) AS invId,
                       id(obj) AS id,
                       obj.nome AS nome,
                       po.codigo AS codPO,
                       unidade.codigo + \" - \" + unidade.sigla AS unidadeOrcamentaria,
                       status.nome AS status,
                       obj.tipo AS tipo,
                       totalPlanejado,
                       totalContratado,
                       totalOrcado,
                       totalAutorizado,
                       totalEmpenhado,
                       totalDisponivel
                
                """;
                
                        
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }

        List<TiraObjetoProjection> tiraObjs = this.neo4jOperations.findAll(cypherQuery, params, TiraObjetoProjection.class);

        return new DataListResult<>(tiraObjs, count);
    }


    public List<Objeto> findObjetoByContaFiltrado(Conta conta, Integer exercicio, Long fonteId) {
        List<Objeto> todosObjetos = findObjetoByConta(conta);

        for(Objeto objeto : todosObjetos) {
            objeto.filtrar(exercicio, fonteId);
        }

        return todosObjetos;
    }
   

}
