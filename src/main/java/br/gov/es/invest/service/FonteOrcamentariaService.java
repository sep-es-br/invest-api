package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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

        FonteOrcamentaria probeFonte = new FonteOrcamentaria();
        probeFonte.setCodigo(codigo);

        Optional<FonteOrcamentaria> optFonte = repository.findBy(Example.of(probeFonte), query -> query.first());
        
        if(optFonte.isPresent()) {
            return optFonte.get();
        } else {
            FonteOrcamentaria novaFonte = new FonteOrcamentaria();

            novaFonte.setCodigo(codigo);
            novaFonte.setNome(nome);

            return repository.save(novaFonte);
        }
    }

    public List<FonteOrcamentaria> findFontesExtras(){
        return repository.findFontesExtra();
    }

}
