package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.repository.FluxoRepository;

@Service
public class FluxoService {
    
    @Autowired
    private FluxoRepository repository;

    public List<Fluxo> findAll() {
        return repository.findAll(Sort.by("codigo", "nome"));
    }


}
