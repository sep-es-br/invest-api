package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.AreaTematica;
import br.gov.es.invest.repository.AreaTematicaRepository;

@Service
public class AreaTematicaService {

    @Autowired
    private AreaTematicaRepository repository;

    public List<AreaTematica> findAll() {

        return repository.findAll();
    }
    
}
