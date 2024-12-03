package br.gov.es.invest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Setor;
import br.gov.es.invest.repository.OrgaoRepository;
import br.gov.es.invest.repository.SetorRepository;

@Service
public class SetorService {
    
    @Autowired
    private SetorRepository repository;

    public Setor findOrCreate(Setor setor){

        Optional<Setor> optSetor = repository.findByGuid(setor.getGuid());

        if(optSetor.isPresent()) {
            return optSetor.get();
        } else {
            return repository.save(setor);
        }

    }

}
