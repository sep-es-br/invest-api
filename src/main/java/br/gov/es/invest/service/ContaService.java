package br.gov.es.invest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.DadosConsolidadosDTO;
import br.gov.es.invest.dto.DadosConsolidadosValores;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ContaRepository;
import br.gov.es.invest.utils.DataListResult;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaService {

    
    private final ContaRepository repository;

    private final Neo4jOperations neo4jOperations;

    public Conta getGenericoByCodUnidade(UnidadeOrcamentaria unidadeOrcamentaria) {

        Conta conta = repository.getGenericoByCodUnidade(unidadeOrcamentaria.getCodigo());
        
        if(conta == null) {
            conta = new Conta();
            conta.setNome("Conta sem PO da Unidade " + unidadeOrcamentaria.getCodigo());
            conta.setUnidadeOrcamentariaImplementadora(unidadeOrcamentaria);
            return conta;
        } else {
            return repository.findById(conta.getId()).get();
        }

    }

    public Conta save(Conta conta){
        return repository.save(conta);
    }

    public List<Conta> findByFiltro(
        String nome, String unidadeId, String planoId,
        Integer anoExercicio, String fonteId, Pageable pageable
    ){
        
        ExampleMatcher matcher = ExampleMatcher.matching();
        Conta contaProbe = new Conta();
        
        if(nome != null){
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        }

        if(planoId != null){ 
            PlanoOrcamentario plano = new PlanoOrcamentario();
            plano.setId(planoId);
            contaProbe.setPlanoOrcamentario(plano);
        } 

        if(unidadeId != null){
            UnidadeOrcamentaria unidade = new UnidadeOrcamentaria();
            unidade.setId(unidadeId);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidade);
        }

        List<Conta> result = repository.findAll(Example.of(contaProbe, matcher));

        if(pageable != null){

            long indexTo = Long.min(pageable.getOffset()+pageable.getPageSize(), result.size()-pageable.getOffset());

            result = result
            .subList(Integer.parseInt(String.valueOf(pageable.getOffset())) , Integer.parseInt( String.valueOf(indexTo)));
        }

        result = repository.filtrarContasForaProcessamento(result.stream().map(c -> c.getId()).toList());

        return repository.findAllById(result.stream().map(c -> c.getId()).toList()) ;



    }

    public Integer countByFilter(String nome, String codUnidade, String codPO, Integer exercicio, String idFonte){
        return findByFiltro(nome, codUnidade, codPO, exercicio, idFonte, null).size();
    }

    public DataListResult<DadosConsolidadosDTO> getDadosConsolidados (
        String tipoDespesa, Integer gnd, Integer exercicio, String idFonte,
        Pageable pageable
    ) {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tipoDespesa", tipoDespesa);
        paramMap.put("gnd", gnd);
        paramMap.put("exercicio", exercicio);
        paramMap.put("idFonte", idFonte);

        String cypherBase = "WITH\r\n" + //
                        "    $tipoDespesa AS _tpDespesa,\r\n" + //
                        "    $gnd AS _gnd,\r\n" + //
                        "    $exercicio AS _exercicio,\r\n" + //
                        "    $idFonte AS _idFonte\r\n" + //
                        "MATCH \r\n" + //
                        "    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),\r\n" + //
                        "    (conta)<-[:CUSTEADO]-(obj:Objeto)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonte:FonteOrcamentaria),\r\n" + //
                        "    (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)\r\n" + //
                        "WHERE\r\n" + //
                        "        _tpDespesa IN LABELS(conta)\r\n" + //
                        "    AND (_gnd IS NULL OR indicada_por.gnd = _gnd)\r\n" + //
                        "    AND custo.anoExercicio = _exercicio\r\n" + //
                        "    AND (_idFonte IS NULL OR elementId(fonte) = _idFonte)\r\n" + //
                        "WITH\r\n" + //
                        "    unidade.codigo AS codUnidade,\r\n" + //
                        "    elementId(conta) AS idUnidade,\r\n" + //
                        "    unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,\r\n" + //
                        "    elementId(po) AS idPO,\r\n" + //
                        "    po.codigo AS codPO,\r\n" + //
                        "    po.nome AS nomePO,\r\n" + //
                        "    'E' IN collect(tipoPlano.sigla) AS projEstrategico,\r\n" + //
                        "    obj.contrato AS contrato,\r\n" + //
                        "    custo.anoExercicio AS anoExercicio,\r\n" + //
                        "    elementId(custo) AS custoId\r\n" + //
                        "WITH codUnidade, \r\n" + //
                        "    {\r\n" + //
                        "        idUnidade: idUnidade,\r\n" + //
                        "        unidadeResponsavel: unidadeResponsavel,\r\n" + //
                        "        idPO: idPO,\r\n" + //
                        "        codPO: codPO,\r\n" + //
                        "        nomePO: nomePO,\r\n" + //
                        "        anoExercicio: anoExercicio,\r\n" + //
                        "        projEstrategico: projEstrategico,\r\n" + //
                        "        contrato: contrato,\r\n" + //
                        "        custoId: custoId,\r\n" + //
                        "        valores: null\r\n" + //
                        "    } as dado\r\n";
        
        String cypherQuery = cypherBase + 
                            "RETURN dado\r\n" + //
                            "ORDER BY codUnidade\r\n";

        String cypherCusto = 
            "MATCH (fonte:FonteOrcamentaria)\r\n" + //
            "OPTIONAL MATCH (custo:Custo)-[indicada_por:INDICADA_POR]->(fonte)\r\n" + //
            "WHERE elementId(custo) = $custoId\r\n" + //
            "RETURN {\r\n" + //
            "    idFonte: elementId(fonte),\r\n" + //
            "    nomeFonte: fonte.nome,\r\n" + //
            "    valorPrevisto: CASE WHEN custo IS NULL THEN 0 ELSE indicada_por.previsto END,\r\n" + //
            "    valorContratado: CASE WHEN custo IS NULL THEN 0 ELSE indicada_por.contratado END\r\n" + //
            "}";

        
        if(pageable != null) {
            cypherQuery += "SKIP $skip LIMIT $limit";
            paramMap.put("skip", pageable.getOffset());
            paramMap.put("limit", pageable.getPageSize());
        }
        String cypherCount = cypherBase + "RETURN count(*)";

        List<DadosConsolidadosDTO> dados = neo4jOperations.findAll(cypherQuery, paramMap, DadosConsolidadosDTO.class);

        for(DadosConsolidadosDTO dado : dados){
            dado.setValores(neo4jOperations.findAll(cypherCusto, Map.of("custoId", dado.getCustoId()), DadosConsolidadosValores.class));
            
        }

        return new DataListResult<>(
            dados, 
            (int) neo4jOperations.count(cypherCount, paramMap)
        );
    }

}
