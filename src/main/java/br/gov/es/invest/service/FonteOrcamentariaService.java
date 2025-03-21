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

    public FonteOrcamentaria findByCod(String cod) {

        FonteOrcamentaria probe = new FonteOrcamentaria();
        probe.setCodigo(cod);

        return repository.findBy(Example.of(probe), q -> q.firstValue());

    }

    public List<FonteOrcamentaria> findFontesExtras(){
        return repository.findFontesExtra();
    }

    public String getCodById(String id){
        Optional<FonteOrcamentaria> optFonte = repository.findById(id);

        return optFonte.map(fonte -> fonte.getCodigo()).orElse(null);
 
    }

}
