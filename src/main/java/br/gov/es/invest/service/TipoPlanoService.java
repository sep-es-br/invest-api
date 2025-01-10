package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.repository.TipoPlanoRepository;

@Service
public class TipoPlanoService {
    
    @Autowired
    private TipoPlanoRepository repository;


    public List<TipoPlano> findAll(){
        
        return repository.findAll();
    }
    

}
