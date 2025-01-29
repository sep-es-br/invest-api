package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Status;
import br.gov.es.invest.repository.StatusRepository;

@Service
public class StatusService {
    
    @Autowired
    private StatusRepository repository;

    public Status findOrCreate(Status status) {
        
        Status statusProbe = new Status();
        statusProbe.setNome(status.getNome());

        Optional<Status> optStatus = repository.findBy(Example.of(statusProbe), q -> q.first());

        return optStatus.orElse(status);

    }

    public List<Status> findAllStatusObjeto(){
        return repository.findAllStatusObjeto();
    }

    public Status findById(String statusId) {
        return repository.findById(statusId).orElse(null);
    }

}
