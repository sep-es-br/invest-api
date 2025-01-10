package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ContaRepository;

@Service
public class ContaService {
    
    @Autowired
    private ContaRepository repository;

    public Conta getGenericoByCodUnidade(UnidadeOrcamentaria unidadeOrcamentaria) {

        Conta conta = repository.getGenericoByCodUnidade(unidadeOrcamentaria.getCodigo());
        
        if(conta == null) {
            conta = new Conta();
            conta.setNome("Conta sem PO da Unidade " + unidadeOrcamentaria.getCodigo());
            conta.setUnidadeOrcamentariaImplementadora(unidadeOrcamentaria);
            conta.setObjetos(new HashSet<>());
            return conta;
        } else {
            return repository.findById(conta.getId()).get();
        }

    }

    public Conta save(Conta conta){
        return repository.save(conta);
    }

    public Conta getByObjetoId(String objetoId){
        Conta investimentoProbe = new Conta();
        Objeto objetoProbe = new Objeto();

        objetoProbe.setId(objetoId);

        investimentoProbe.setObjetos(Set.of(objetoProbe));

        Example<Conta> example = Example.of(
            investimentoProbe, 
            ExampleMatcher.matching()
            .withMatcher("objetos", ExampleMatcher.GenericPropertyMatcher::contains));
        
        Conta result = repository.findBy(example, query -> query.oneValue());

        return result;


    }

}
