package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Ano;
import br.gov.es.invest.repository.AnoRepository;
import br.gov.es.invest.repository.CustoRepository;
import br.gov.es.invest.repository.ExecucaoOrcamentariaRepository;

@Service
public class InfosService {
    

    @Autowired
    private AnoRepository anoRepository;

    public List<String> getAllAnos() {

        List<Ano> anos = anoRepository.findAll(Sort.by("ano"));


        return anos.stream().map(ano -> ano.getAno()).toList();
    }





}
