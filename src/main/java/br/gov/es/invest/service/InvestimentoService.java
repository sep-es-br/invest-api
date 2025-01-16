package br.gov.es.invest.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.VinculadaPor;
import br.gov.es.invest.repository.InvestimentoRepository;

@Service
public class InvestimentoService {
    
    @Autowired
    private InvestimentoRepository repository;

    @Autowired
    private ObjetoService objetoService;


    public void saveAll(List<Investimento> investimentos) {
        repository.saveAll(investimentos);
    }

    public Investimento save(Investimento investimento) {
        return repository.save(investimento);
    }

    public List<Investimento> findAllByFilterValores(
            String nome, String codUnidade, String codPO,
            Integer exercicio, String idFonte, Pageable pageable
        ) {

        // filtro em 2 etapas, primeiro filtra por investimento
        Investimento investimentoProbe = new Investimento();

        if(codPO != null) {
            PlanoOrcamentario planoProbe = new PlanoOrcamentario();
            planoProbe.setId(codPO);
            investimentoProbe.setPlanoOrcamentario(planoProbe);
        }

        if(codUnidade != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(codUnidade);
    
            investimentoProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        List<Investimento> investimentosFiltrados;
        
        if(pageable == null) {
            investimentosFiltrados = repository.findBy(
                    Example.of(investimentoProbe), 
                    query -> query.all());
        } else {
            // existe algum bug bizarro que faz quebrar a query normal com
            // alguns UOs então é feita de forma mais indireta e informal
            investimentosFiltrados = repository.findByUoPo(codUnidade, codPO, pageable);
            investimentosFiltrados = repository.findAllById(investimentosFiltrados.stream().map(inv -> inv.getId()).toList());
        }

        // filtra usando o java para atender a nescessidade de usar uma string "limpa"
        if(nome != null){
            investimentosFiltrados = investimentosFiltrados.stream().filter(
                inv -> clean(inv.getNome()).contains(clean(nome))
            ).toList();
        }

        /*
         * se utilizasse a API normal ele traria todos os custos do investimento que tem aquele custo, 
         * não é o que eu quero por isso eu filtro "manualmente"
         */

        for (Investimento investimento : investimentosFiltrados) {
            List<ExecucaoOrcamentaria> execsFiltrados = investimento.getExecucoesOrcamentaria();

            if(exercicio != null)
                execsFiltrados = execsFiltrados.stream()
                            .filter(exec -> exec.getAnoExercicio().equals(exercicio)).toList();

            for(ExecucaoOrcamentaria execucaoOrcamentaria : execsFiltrados) {
                
                Set<VinculadaPor> vinculadaFiltrada = execucaoOrcamentaria.getVinculadaPor();

                if(idFonte != null)
                    vinculadaFiltrada = vinculadaFiltrada.stream()
                                    .filter(vinculada -> vinculada.getFonteOrcamentaria().getId().equals(idFonte))
                                    .collect(Collectors.toSet());
                
                execucaoOrcamentaria.setVinculadaPor(new HashSet<>(vinculadaFiltrada));

            }

            investimento.setExecucoesOrcamentaria(execsFiltrados);
            
            
            for(Objeto objeto : objetoService.findObjetoByConta(investimento)) {
                List<Custo> custosFiltrados = objeto.getCustosEstimadores();

                if( exercicio != null)
                    custosFiltrados = custosFiltrados.stream()
                                .filter(custo -> custo.getAnoExercicio().equals(exercicio)).toList();

                for(Custo custo : custosFiltrados){
                    Set<IndicadaPor> indicadaFiltrado = custo.getIndicadaPor();

                    if(idFonte != null)
                        indicadaFiltrado = indicadaFiltrado.stream()
                                                    .filter(indicada -> indicada.getFonteOrcamentaria().getId().equals(idFonte))
                                                    .collect(Collectors.toSet());
                    
                    custo.setIndicadaPor(indicadaFiltrado);

                }

                objeto.setCustosEstimadores(new ArrayList<>(custosFiltrados));
            }
        }

        return investimentosFiltrados;

    }

    public String clean(String input) {
            String normalized = Normalizer.normalize(input, Normalizer.Form.NFD); 
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }

    public int ammountByFilterValores(
            String nome, String codUnidade, String codPO, Integer exercicio, String idFonte
        ){
        return this.findAllByFilterValores(nome, codUnidade, codPO, exercicio, idFonte, null).size();
    }

    public void addExecucao (String investimentoId, String execId) {

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
}
