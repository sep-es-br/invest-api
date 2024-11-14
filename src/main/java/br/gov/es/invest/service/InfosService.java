package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.repository.CustoRepository;
import br.gov.es.invest.repository.ExecucaoOrcamentariaRepository;
import br.gov.es.invest.repository.PlanoOrcamentarioRepository;

@Service
public class InfosService {
    

    @Autowired
    private CustoRepository custoRepository;

    @Autowired
    private ExecucaoOrcamentariaRepository execucaoRepository;

    @Autowired
    private PlanoOrcamentarioRepository planoRepository;

    public List<String> getAllAnos() {

        List<String> anosCusto = custoRepository.findAllAnos();
        List<String> anosExec = execucaoRepository.findAllAnos();
        
        Set<String> anosCustoSet = new HashSet<>(anosCusto);
        Set<String> anosExecSet = new HashSet<>(anosExec);

        HashSet<String> todosAnos = new HashSet<>(anosCustoSet);
        todosAnos.addAll(anosExecSet);

        return todosAnos.stream().sorted((s1, s2) -> s1.compareTo(s2)).toList();
    }





}
