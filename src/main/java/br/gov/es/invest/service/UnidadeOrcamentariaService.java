package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.UnidadeOrcamentariaRepository;

@Service
public class UnidadeOrcamentariaService {
    
    @Autowired
    private UnidadeOrcamentariaRepository repository;

    public void saveAll(List<UnidadeOrcamentaria> unidades) {
        repository.saveAll(unidades);
    }

    public List<UnidadeOrcamentaria> getAll() {
        return repository.findAll();
    }

    public List<UnidadeOrcamentariaDTOProjection> getAllSimples() {
        return repository.findAllUnidades();
    }

    public String getCodById(String idUnidade) {
        return repository.getCodById(idUnidade);
    }

    public UnidadeOrcamentaria findBySigla(String sigla) {

        UnidadeOrcamentaria probe = new UnidadeOrcamentaria();
        probe.setSigla(sigla);

        ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreCase("sigla");

        return this.repository.findBy(Example.of(probe, matcher), query -> query.firstValue());

    }

}
