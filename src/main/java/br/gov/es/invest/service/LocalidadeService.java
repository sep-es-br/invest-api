package br.gov.es.invest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Localidade;
import br.gov.es.invest.repository.LocalidadeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalidadeService {
    
    private final LocalidadeRepository repository;
    
    public Optional<Localidade> findById(Long id) {
        return repository.findById(id);
    }

    public List<Localidade> findAll() {
   
        return repository.findAll();
    }

}
