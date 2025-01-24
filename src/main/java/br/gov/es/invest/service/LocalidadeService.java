package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Localidade;
import br.gov.es.invest.repository.LocalidadeRepository;

@Service
public class LocalidadeService {
    
    @Autowired
    private LocalidadeRepository repository;


    public List<Localidade> findAll() {
   
        return repository.findAll();
    }

}
