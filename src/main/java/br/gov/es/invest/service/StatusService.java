package br.gov.es.invest.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.repository.StatusRepository;

@Service
public class StatusService {
    
    @Autowired
    private StatusRepository repository;

    private ObjetoService objetoService;

    public Status findOrCreate(Status status) {
        
        Status statusProbe = new Status();
        statusProbe.setNome(status.getNome());

        Optional<Status> optStatus = repository.findBy(Example.of(statusProbe), q -> q.first());

        return optStatus.orElse(status);

    }

    public List<Status> findAllStatusObjeto(){
        return repository.findAllStatusObjeto();
    }

    public Optional<Status> getByStatusId(String statusId){

        Status statusProbe = new Status();
        statusProbe.setStatusId(StatusEnum.valueOf(statusId));

        return repository.findBy(Example.of(statusProbe), q -> q.first());

    }

    public Status findById(String statusId) {
        return repository.findById(statusId).orElse(null);
    }

    public void aplicarStatus(Objeto objeto, Status status) {
        
        EmStatus emStatus = new EmStatus();
        emStatus.setStatus(status);
        emStatus.setTimestamp(ZonedDateTime.now());

        objeto.setEmStatus(emStatus);

        objetoService.save(objeto);
    }

    @Autowired
    public void setObjetoService(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }
}
