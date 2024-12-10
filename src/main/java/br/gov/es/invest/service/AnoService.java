package br.gov.es.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Ano;
import br.gov.es.invest.repository.AnoRepository;

@Service
public class AnoService {
    
    @Autowired
    private AnoRepository repository;

    public Ano findOrCreate(String ano){

        Ano optAno = repository.getByAno(ano);

        if(optAno != null) {
            return optAno;
        } else {
            return new Ano(ano); 
        }
        
    }

}
