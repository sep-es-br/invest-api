package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.repository.EtapaRepository;

@Service
public class EtapaService {
    
    @Autowired
    private EtapaRepository etapaRepository;

    public List<Etapa> findAll(){
        return etapaRepository.findAll();
    }

    public Optional<Etapa> findById(String id) {
        return etapaRepository.findById(id);
    }

}
