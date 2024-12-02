package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.repository.GrupoRepository;

@Service
public class GrupoService {
    
    @Autowired
    private GrupoRepository repository;

    public List<Grupo> findAll(String nome, Pageable pageable) {
        
        return repository.findAllByFilter(nome, pageable);

    }

    public Optional<Grupo> findById(String id){
        return repository.findById(id);
    }

    public Grupo save(Grupo grupo){
        return repository.save(grupo);
    }

    public Grupo delete (String grupoId){
        Grupo deletedGrupo = repository.findById(grupoId).orElse(null);

        if(deletedGrupo != null) {
            repository.delete(deletedGrupo);
        }

        return deletedGrupo;
    }

}
