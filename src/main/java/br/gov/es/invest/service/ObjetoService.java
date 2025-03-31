package br.gov.es.invest.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.dto.OrdemItemDto;
import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
import br.gov.es.invest.dto.projection.TiraObjetoProjection;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ObjetoRepository;
import br.gov.es.invest.utils.DataListResult;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObjetoService {
    
    private final ObjetoRepository repository;

    private final Neo4jOperations neo4jOperations;

    
    private final InvestimentoService investimentoService;
    private final UnidadeOrcamentariaService unidadeService;
    private final PlanoOrcamentarioService planoService;
    private final ContaService contaService;
    
    private final StatusService statusService;

    private final FluxoService fluxoService;



    public void saveAll(List<Objeto> objetos) {
        repository.saveAll(objetos);
    }

    public Objeto save(Objeto objeto) {
        UnidadeOrcamentaria unidade = unidadeService.findOrCreateByCod(objeto.getConta().getUnidadeOrcamentariaImplementadora());
        
        // define o Investimento que vai ser associado

        // se não tiver PO usa o investimento generico

        Conta conta = null;
        if(objeto.getConta().getPlanoOrcamentario() == null) {
            conta = contaService.getGenericoByCodUnidade(unidade);
        } else { // se não, busca o investimento

            Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(
                objeto.getConta().getUnidadeOrcamentariaImplementadora().getCodigo(), 
                objeto.getConta().getPlanoOrcamentario().getCodigo()
            );
            Investimento investimento;

            if(optInvestimento.isEmpty()){ // se não existir, cria um novo

                    PlanoOrcamentario plano = planoService.findOrCreateByCod(objeto.getConta().getPlanoOrcamentario());

                    investimento = new Investimento();
                    investimento.setNome(objeto.getNome());
                    investimento.setUnidadeOrcamentariaImplementadora(unidade);
                    investimento.setPlanoOrcamentario(plano);
            } else { // se existir usa o existente
                investimento = optInvestimento.get();
            }
            
            conta = investimento;

        }
        
        objeto.setConta(conta);

        if(objeto.getEmStatus() == null) {
            
            Status novoStatus = statusService.getByStatusId(StatusEnum.SOLICITADO.name()).get();

            EmStatus emStatus = new EmStatus();

            emStatus.setStatus(novoStatus);
            emStatus.setTimestamp(ZonedDateTime.now());

            objeto.setEmStatus(emStatus);

            Fluxo fluxo = fluxoService.findByFluxoId("avaliacaoPip");

            EmEtapa emEtapa = new EmEtapa();
            emEtapa.setAtividade("Avaliar Solicitação");
            emEtapa.setDevolvido(false);
            emEtapa.setEtapa(fluxo.getEtapaInicial());
            
            objeto.setEmEtapa(emEtapa);
            
        }
        
        if(objeto.getId() != null) {
            List<TipoPlano> filhoAtuais = repository.findById(objeto.getId()).get().getTiposPlano();

            List<String> idsFilhoFinal = objeto.getTiposPlano().stream().map( filho -> filho.getId()).toList();

            List<TipoPlano> orfaos = filhoAtuais.stream().filter(filho -> !idsFilhoFinal.contains(filho.getId())).toList();

            if (!orfaos.isEmpty()) {
                repository.removerTipos(orfaos.stream().map(orfao -> orfao.getId()).toList());
            }

        }

        return repository.save(objeto);
    }

    public Objeto findById(String id){
        return repository.findById(id).orElse(null);
    }

    public DataListResult<ObjetoTiraDTO> getAllListByFilter(Integer exercicio, String nome, List<String> idUnidade, List<String> idPo, String statusId, String fonteId, List<OrdemItemDto> ordem, Pageable pageable){
        
        String cypherBase = """
                MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status),
                    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta)
                WHERE
                    ($nome IS NULL OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))
                    AND ($idsUnidade IS NULL OR elementId(unidade) IN $idsUnidade)
                    AND ($idStatus IS NULL OR elementId(status) = $idStatus)
                OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)
                WHERE
                    ($idsPo IS NULL OR elementId(plano) IN $idsPo)
                CALL(conta){
                    MATCH (conta)
                    OPTIONAL MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vp:VINCULADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE
                        exec.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = elementId(fonte))
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
                        AND ($idFonte IS NULL OR $idFonte = elementId(fonte))
                    RETURN
                        sum(ip.previsto) AS totalPrevisto, 
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

        String cypherQuery = cypherBase +
                        """
                        RETURN
                            elementId(obj) AS id,
                            unidade.codigo AS codUnidade,
                            unidade.sigla AS siglaUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            plano.codigo AS codPO,
                            obj.nome AS nome,
                            obj.tipo AS tipo,
                            totalPrevisto,
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

    public List<ObjetoTiraDTO> getAllListByFilterEmProcessamento(Integer exercicio, String nome, List<String> idUnidade, List<String> idPo, String statusId, String etapaId, String fonteId, Pageable pageable){
         
        String cypherBase = """
                MATCH (conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status),
                    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta)
                WHERE
                    ($nome IS NULL OR apoc.text.clean(obj.nome) contains apoc.text.clean($nome))
                    AND ($idsUnidade IS NULL OR elementId(unidade) IN $idsUnidade)
                    AND ($idStatus IS NULL OR elementId(status) = $idStatus)
                OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)
                WHERE
                    ($idsPo IS NULL OR elementId(plano) IN $idsPo)
                CALL(conta){
                    MATCH (conta)
                    OPTIONAL MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vp:VINCULADA_POR]->(fonte:FonteOrcamentaria)
                    WHERE
                        exec.anoExercicio = $exercicio
                        AND ($idFonte IS NULL OR $idFonte = elementId(fonte))
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
                        AND ($idFonte IS NULL OR $idFonte = elementId(fonte))
                    RETURN
                        sum(ip.previsto) AS totalPrevisto, 
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

        String cypherQuery = cypherBase +
                        """
                        RETURN
                            elementId(obj) AS id,
                            unidade.codigo AS codUnidade,
                            unidade.sigla AS siglaUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            plano.codigo AS codPO,
                            obj.nome AS nome,
                            obj.tipo AS tipo,
                            totalPrevisto,
                            totalContratado,
                            totalOrcado,
                            totalAutorizado,
                            totalEmpenhado,
                            totalDisponivel,
                            status.nome AS status
                        ORDER BY codUnidade, codPO
                        
                        """;

        // if(ordem != null && !ordem.isEmpty())
        //     cypherQuery += "ORDER BY " + ordem.stream().map(item -> item.campo() + " " + item.direcao()).collect(Collectors.joining(", ")) + "\n";
       
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

        


        return neo4jOperations.findAll(cypherQuery, params, ObjetoTiraDTO.class);

    }

    public List<Objeto> getAllByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String statusId, Pageable pageable) {
        
        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta();
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
                planoProbe.setId(idPo);
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

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe));

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
        String nome, String unidadeId, String planoId,
        Integer anoExercicio, String fonteId
    ) {

        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta();
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
                planoProbe.setId(planoId);
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe));

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(anoExercicio, fonteId);
        }

        return objetoFiltrado;
    }

    public void updateStatus(String objId, Status novoStatus) {
        Optional<Objeto> optObjeto = repository.findById(objId);
        
        if(optObjeto.isEmpty()) return;

        Objeto obj = optObjeto.get();
        obj.getEmStatus().setStatus(novoStatus);
        obj.getEmStatus().setTimestamp(ZonedDateTime.now());

        repository.save(obj);

    }

    public Objeto getByCusto(Custo custo){
        return repository.getByCusto(custo.getId());
    }

    public Optional<Objeto> getById(String id, boolean updateStatus) {
        Optional<Objeto> optObjeto = repository.findById(id);
        
        if(optObjeto.isPresent() 
            && optObjeto.get().getEmStatus().getStatus().getStatusId().equals(StatusEnum.SOLICITADO) 
            && updateStatus){
            Status novoStatus = statusService.getByStatusId(StatusEnum.EM_ANALISE.name()).get();

            statusService.aplicarStatus(optObjeto.get(), novoStatus);
            optObjeto = repository.findById(id);
        }

        return optObjeto;
    }

    public Optional<Objeto> getById(String id) {
        return this.getById(id, false);
    }

    public List<Objeto> getAllByIds(List<String> ids) {
        return repository.findAllById(ids);
    }

    public int countByFilter(String nome, String codUnidade, String codPO, String status, Integer exercicio) {

        return repository.countByFilter(nome, codUnidade, codPO, status, exercicio);
    }

    public int countByInvestimentoFilter(String nome, List<String> codUnidade, List<String> codPO, Integer exercicio) {
        return repository.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio);
    }

    public List<Status> findStatusCadastrados() {
        return repository.findStatusCadastrados();
    }

    public Objeto removerObjeto(String objetoId) {
        Optional<Objeto> optObjeto = repository.findById(objetoId);

        if(optObjeto.isEmpty())
            return null;

        repository.removerObjeto(objetoId);
        return optObjeto.get();
    }

    public List<Objeto> findObjetoByConta(Conta conta) {
        return this.findObjetoByConta(conta.getId());
    }

    public List<Objeto> findObjetoByConta(String contaId) {
        Objeto objetoProbe = new Objeto();
        Conta contaProbe = new Conta();
        contaProbe.setId(contaId);
        objetoProbe.setConta(contaProbe);

        return repository.findAll(Example.of(objetoProbe));
    }

    public DataListResult<TiraObjetoProjection> findObjetoCadastradoByContaBy(
            String idConta, Integer exercicio, String idFonte, Integer gnd, Pageable pageable
    ) {
        String cypher = "MATCH (inv:Investimento)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status),\r\n" + //
                        "        (po:PlanoOrcamentario)-[:ORIENTA]->(inv)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r\n" + //
                        "WHERE NOT EXISTS((obj)-[:EM]->(:Etapa))\r\n" + //
                        "    AND (elementId(inv) = $idConta)\r\n" + //
                        "CALL (obj) {\r\n" + //
                        "    MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
                        "    WHERE ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte)\r\n" + //
                        "        AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio)\r\n" + //
                        "        AND ($gnd IS NULL OR indicada_por.gnd = $gnd)\r\n" + //
                        "    RETURN \r\n" + //
                        "        ($gnd IS NULL OR indicada_por.gnd = $gnd) AS gnd,\r\n" + //
                        "        sum(indicada_por.previsto) AS totalPrevisto,\r\n" + //
                        "        sum(indicada_por.contratado) AS totalContratado \r\n" + //
                        "}\r\n" + //
                        "CALL (inv) {\r\n" + //
                        "    MATCH (inv)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
                        "    WHERE ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)\r\n" + //
                        "        AND ($exercicio IS NULL OR exec.anoExercicio = $exercicio)\r\n" + //
                        "        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)\r\n" + //
                        "    RETURN\r\n" + //
                        "        sum(vinculada_por.orcado) AS totalOrcado,\r\n" + //
                        "        sum(vinculada_por.autorizado) AS totalAutorizado,\r\n" + //
                        "        sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS totalEmpenhado,\r\n" + //
                        "        sum(vinculada_por.dispSemReserva) AS totalDisponivel\r\n" + //
                        "}\r\n";

        HashMap<String, Object> params = new HashMap<>();
        params.put("idConta", idConta);
        params.put("exercicio", exercicio);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);

        String cypherCount = cypher + "RETURN COUNT(*)";

        int count = (int) this.neo4jOperations.count(cypherCount, params);
        
        String cypherQuery = cypher + "RETURN\r\n" + //
                        "        elementId(obj) AS id,\r\n" + //
                        "        obj.nome AS nome,\r\n" + //
                        "        po.codigo AS codPO,\r\n" + //
                        "        unidade.codigo + \" - \" + unidade.sigla AS unidadeOrcamentaria,\r\n" + //
                        "        status.nome AS status,\r\n" + //
                        "        obj.tipo AS tipo,\r\n" + //
                        "        totalPrevisto,\r\n" + //
                        "        totalContratado,\r\n" + //
                        "        totalOrcado,\r\n" + //
                        "        totalAutorizado,\r\n" + //
                        "        totalEmpenhado,\r\n" + //
                        "        totalDisponivel\r\n";
                        
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }

        List<TiraObjetoProjection> tiraObjs = this.neo4jOperations.findAll(cypherQuery, params, TiraObjetoProjection.class);

        return new DataListResult<>(tiraObjs, count);
    }


    public List<Objeto> findObjetoByContaFiltrado(Conta conta, Integer exercicio, String fonteId) {
        List<Objeto> todosObjetos = findObjetoByConta(conta);

        for(Objeto objeto : todosObjetos) {
            objeto.filtrar(exercicio, fonteId);
        }

        return todosObjetos;
    }
   

}
