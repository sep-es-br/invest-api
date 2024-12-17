package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public List<FonteOrcamentaria> findAll(){
        return repository.findAll(Sort.by("nome"));
    }

    public FonteOrcamentaria findOrCreate(String codigo, String nome) {

        FonteOrcamentaria optFonte = repository.findByCodigo(codigo);
        
        if(optFonte != null) {
            return optFonte;
        } else {
            FonteOrcamentaria novaFonte = new FonteOrcamentaria();

            novaFonte.setCodigo(codigo);
            novaFonte.setNome(nome);

            return novaFonte;
        }
    }

}
