package br.gov.es.invest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Papel;
import br.gov.es.invest.repository.PapelRepository;
import java.util.List;

@Service
public class PapelService {
    
    @Autowired
    private PapelRepository repository;

    public Optional<Papel> findByGuid(String guid) {
        
        Papel papelProbe = new Papel();
        papelProbe.setGuid(guid);

        return repository.findBy(Example.of(papelProbe), q -> q.first());

    }
    
    public void deleteAllById(List<String> ids) {
        repository.deleteAllById(ids);
    }
    
    public Papel save(Papel papel){
        if(papel == null) return null;
        
        return repository.save(papel);
    }


}
