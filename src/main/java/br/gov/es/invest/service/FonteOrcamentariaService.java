package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.repository.FonteOrcamentariaRepository;

@Service
public class FonteOrcamentariaService {
    
    @Autowired
    private FonteOrcamentariaRepository repository;

    public void saveAll(List<FonteOrcamentaria> fontes) {
        repository.saveAll(fontes);
    }

}
