package br.gov.es.invest.service;

import br.gov.es.invest.dto.OrdemItemDto;
import br.gov.es.invest.dto.investimento.InvestimentoListaDto;
import br.gov.es.invest.dto.projection.TiraInvestimentoProjection;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.InvestimentoRepository;
import br.gov.es.invest.utils.DataListResult;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
public class InvestimentoService {
    
    @Autowired
    private InvestimentoRepository repository;

    @Autowired
    private Neo4jOperations neo4jOperations;

    public void saveAll(List<Investimento> investimentos) {
        repository.saveAll(investimentos);
    }

    public Investimento save(Investimento investimento) {
        return repository.save(investimento);
    }
    
    public Optional<Investimento> getById(Long id) {
        return repository.findById(id);
    }
    
    public DataListResult<TiraInvestimentoProjection> findAllTiraBy(
            String nome, List<Long> codUnidade, List<Long> codPO,
            Integer exercicio, Long idFonte, Integer gnd, List<OrdemItemDto> ordem,
            Pageable pageable
        ) {

            String cypherBase = """
                            MATCH (inv:Investimento)<-[:CUSTEADO]-(obj:Objeto),
                                    (po:PlanoOrcamentario)-[:ORIENTA]->(inv)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)
                            WHERE ($idPo IS NULL OR id(po) IN $idPo)
                                AND ( $idUnidade IS NULL OR id(unidade) IN $idUnidade )
                                AND NOT EXISTS((obj)-[:EM]->(:Etapa))
                                AND ($nome IS NULL OR apoc.text.clean(po.nome) CONTAINS apoc.text.clean($nome))
                            CALL (obj) {
                                MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                                WHERE ($idFonte IS NULL OR id(fonteCusto) = $idFonte)
                                    AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio)
                                    AND ($gnd IS NULL OR indicada_por.gnd = $gnd)
                                RETURN 
                                    ($gnd IS NULL OR indicada_por.gnd = $gnd) AS gnd,
                                    sum(indicada_por.previsto) AS totalPrevisto, 
                                    sum(indicada_por.contratado) AS totalContratado 
                            } 
                            CALL (inv) {
                                MATCH (inv)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                                WHERE ($idFonte IS NULL OR id(fonteExec) = $idFonte)
                                    AND ($exercicio IS NULL OR exec.anoExercicio = $exercicio)
                                    AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                                RETURN
                                    sum(vinculada_por.orcado) AS totalOrcado,
                                    sum(vinculada_por.autorizado) AS totalAutorizado, 
                                    sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS totalEmpenhado, 
                                    sum(vinculada_por.dispSemReserva) AS totalDisponivel
                            }
                            """ ;

        String cypherQuery = cypherBase + 
                            """
                            RETURN
                                id(inv) AS id,
                                po.nome AS nome, 
                                po.codigo AS codPO,
                                unidade.codigo AS codUnidade,
                                unidade.sigla AS siglaUnidade,
                                unidade.codigo + " - " + unidade.sigla AS unidadeOrcamentaria, 
                                sum(totalPrevisto) AS totalPrevisto,
                                sum(totalContratado) AS totalContratado,
                                totalOrcado,
                                totalAutorizado, 
                                totalEmpenhado, 
                                totalDisponivel

                            """;

        if(ordem != null && !ordem.isEmpty() )
            cypherQuery += "ORDER BY " + 
                            ordem.stream().map(item -> item.campo() + " " + item.direcao()).collect(Collectors.joining(", ")) + 
                            "\n";
        
        String cypherCount = cypherBase + 

                """
                RETURN
                    COUNT(DISTINCT inv)
                """ ;




        HashMap<String, Object> params = new HashMap<>();
        params.put("idPo", codPO);
        params.put("idUnidade", codUnidade);
        params.put("nome", nome);
        params.put("idFonte", idFonte);
        params.put("exercicio", exercicio);
        params.put("gnd", gnd);

        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }

        List<TiraInvestimentoProjection> data = this.neo4jOperations.findAll(cypherQuery, params, TiraInvestimentoProjection.class);

        int count = (int) this.neo4jOperations.count(cypherCount, params);

        return new DataListResult<>(data, count);

    }
    
    public DataListResult<InvestimentoListaDto> findAllLista(
            String termo,
            List<Long> idsUos,
            Pageable pageable
    ){
        final String queryBase = """
                                 MATCH (uo:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Investimento)<-[:ORIENTA]-(po:PlanoOrcamentario)
                                 OPTIONAL MATCH (conta)<-[:CUSTEADO]-(objeto:Objeto)   
                                 WITH *
                                 WHERE 
                                   ($uoIds IS NULL OR id(uo) = $uoIds)
                                   AND (
                                       $termo IS NULL OR
                                       apoc.text.clean(coalesce(conta.nome, po.nome)) CONTAINS apoc.text.clean($termo) OR
                                       uo.codigo CONTAINS $termo OR
                                       uo.sigla CONTAINS toUpper($termo) OR
                                       po.codigo CONTAINS $termo
                                   )
                                 CALL (conta) {
                                   MATCH (conta)-[]-(exec:ExecucaoOrcamentaria)-[vlr]-(:FonteOrcamentaria)
                                   WHERE exec.anoExercicio = date().year
                                   RETURN 
                                     sum(vlr.autorizado) AS autorizado,
                                     sum(vlr.orcado) AS orcado,
                                     sum(reduce(total = 0, e IN vlr.empenhado | total + e)) AS empenhado,
                                     sum(vlr.dispSemReserva) as dispSemReserva
                                 }
                                 CALL (objeto) {
                                   MATCH (objeto)-[]-(custo:Custo)-[vlr]-(:FonteOrcamentaria)
                                   WHERE custo.anoExercicio = date().year
                                   RETURN 
                                     sum(vlr.previsto) as previsto,
                                     sum(vlr.contratado) as contratado
                                 }
                                 
                                 """;
        HashMap<String, Object> params = new HashMap<>();
        params.put("uoIds", idsUos);
        params.put("termo", termo);
        
        String query = queryBase 
              + """
                RETURN DISTINCT
                  id(conta) as id,
                  uo.codigo as codUnidade,
                  uo.sigla as siglaUnidade,
                  po.codigo as codPO,
                  coalesce(conta.nome, po.nome) as nome,
                  head([l IN labels(conta) WHERE l <> 'Conta']) as tipo,
                  previsto as totalPrevisto,
                  contratado as totalContratado,
                  autorizado as totalAutorizado,
                  orcado AS totalOrcado,
                  empenhado AS totalEmpenhado,
                  dispSemReserva as totalDisponivel
                
                """;
        
        if(pageable != null) {
            query = query 
              + """
                SKIP $skip
                LIMIT $limit
                """;
            
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());
        }
        
        String countQuery = queryBase
              + """
                RETURN COUNT(DISTINCT conta)
                """;
        
        int count = (int) this.neo4jOperations.count(countQuery, params);
        
        List<InvestimentoListaDto> resultQuery = this.neo4jOperations.findAll(query, params, InvestimentoListaDto.class);
        
        return new DataListResult<>(resultQuery, count);
        
        
    }

    public String clean(String input) {
            String normalized = Normalizer.normalize(input, Normalizer.Form.NFD); 
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }

    public void addExecucao (Long investimentoId, Long execId) {

        this.repository.addExecucao(investimentoId, execId);

    }

    public Optional<Investimento> getByCodUoPo(String codUo, String codPo) {

        PlanoOrcamentario probePlano = new PlanoOrcamentario();
        probePlano.setCodigo(codPo);

        UnidadeOrcamentaria probeUnidade = new UnidadeOrcamentaria();
        probeUnidade.setCodigo(codUo);
        
        Investimento probeInvestimento = new Investimento();
        probeInvestimento.setPlanoOrcamentario(probePlano);
        probeInvestimento.setUnidadeOrcamentariaImplementadora(probeUnidade);


        return repository.findBy(Example.of(probeInvestimento), query -> query.first());
    }
    
    public void removerInvestimento(Long idInvestimento) {
        this.repository.removerInvestimento(idInvestimento);
    }
    
    public Long checarPar(String codPo, String codUo) {
        return this.repository.checarPar(codPo, codUo);
    }
}
