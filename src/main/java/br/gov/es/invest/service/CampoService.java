package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Campo;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.repository.CampoRepository;

@Service
public class CampoService {
    
    @Autowired
    private CampoRepository campoRepository;

    public List<Campo> findAll(){
 
        return campoRepository.findAll(Sort.by("campoId"));
    }


}
