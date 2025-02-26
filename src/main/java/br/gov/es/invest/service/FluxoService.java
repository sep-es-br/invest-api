package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.AcaoEnum;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.repository.FluxoRepository;

@Service
public class FluxoService {
    
    @Autowired
    private FluxoRepository repository;

    public List<Fluxo> findAll() {
        return repository.findAll(Sort.by("codigo", "nome"));
    }

    public Fluxo findWithEtapa(String etapaId) {
        Etapa etapaProbe = new Etapa();
        etapaProbe.setId(etapaId);

        Fluxo fluxoProbe = new Fluxo();
        fluxoProbe.setEtapas(Collections.singletonList(etapaProbe));

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("etapas", ExampleMatcher.GenericPropertyMatchers.contains());

        return repository.findBy(Example.of(fluxoProbe, matcher), q -> q.firstValue());

    }

    public Fluxo findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Fluxo findByFluxoId(String fluxoId) {
        Fluxo fluxoProbe = new Fluxo();
        fluxoProbe.setFluxoId(fluxoId);

        return repository.findBy(Example.of(fluxoProbe), q -> q.firstValue());
    }

    public Fluxo salvarFluxo(Fluxo fluxo) {
        return repository.save(fluxo);
    }



}
