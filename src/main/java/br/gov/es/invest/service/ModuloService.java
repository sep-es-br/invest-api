package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.repository.ModuloRepository;

@Service
public class ModuloService {
    
    @Autowired
    private ModuloRepository repository;

    public Set<Modulo> findAll(){
        return new HashSet<Modulo>(repository.findAll());
    }

}
