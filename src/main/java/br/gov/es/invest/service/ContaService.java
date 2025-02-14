package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.MatcherConfigurer;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ContaRepository;

@Service
public class ContaService {
    
    @Autowired
    private ContaRepository repository;

    public ObjetoService objetoService;

    @Autowired
    public void setObjetoService(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }

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

}
