package br.gov.es.invest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.repository.SetorRepository;

@Service
public class SetorService {
    
    @Autowired
    private SetorRepository repository;

    public Setor findOrCreate(Setor setor, Orgao orgao){

        Setor probe = new Setor();
        probe.setGuid(setor.getGuid());

        Optional<Setor> optSetor = repository.findBy(Example.of(probe), query -> query.first());

        if(optSetor.isPresent()) {
            return optSetor.get();
        } else {
            setor.setOrgao(orgao);
            return repository.save(setor);
        }

    }
    
    public Setor save(Setor setor){
        if(setor == null) return null;
        
        return repository.save(setor);
    }

    public Optional<Setor> findByGuid(String guid) {
        Setor setorProbe = new Setor(guid, null, null, null);
                
        return repository.findBy(Example.of(setorProbe), q -> q.first());
    }

    public Setor findOrCreate(Setor setor){

        Setor probe = new Setor();
        probe.setGuid(setor.getGuid());

        Optional<Setor> optSetor = repository.findBy(Example.of(probe), query -> query.first());

        return optSetor.orElse(setor);

    }

    

}
