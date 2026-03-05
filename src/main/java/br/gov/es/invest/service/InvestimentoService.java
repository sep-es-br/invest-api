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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        
        Page<TiraInvestimentoProjection> page = this.repository.findAllByFilter(nome, codUnidade, codPO, exercicio, idFonte, gnd, pageable);

        return new DataListResult<>(page);

    }
    
    public DataListResult<InvestimentoListaDto> findAllLista(
            String termo,
            List<Long> idsUos,
            Pageable pageable
    ){
        final String queryBase = """
                                 MATCH (uo:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Investimento)<-[:ORIENTA]-(po:PlanoOrcamentario)
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
                                 CALL (conta) {
                                   MATCH (conta)<-[:CUSTEADO]-(objeto:Objeto)-[]-(custo:Custo)-[vlr]-(:FonteOrcamentaria)
                                   WHERE custo.anoExercicio = date().year
                                   RETURN 
                                     sum(vlr.planejado) as planejado,
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
                  planejado as totalPlanejado,
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
