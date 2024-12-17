package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.repository.CustoRepository;
import br.gov.es.invest.repository.ExecucaoOrcamentariaRepository;

@Service
public class AnoService {
    
    @Autowired
    private CustoRepository custoRepository;

    @Autowired
    private ExecucaoOrcamentariaRepository execucaoOrcamentariaRepository;

    public Set<Integer> getAllAnos() {
        
        Set<Integer> anosCusto = custoRepository.getAnosExercicio();
        Set<Integer> anosExecucoes = execucaoOrcamentariaRepository.getAnosExercicio();

        HashSet<Integer> todosAnos = new HashSet<>();

        todosAnos.addAll(anosCusto);
        todosAnos.addAll(anosExecucoes);

        return todosAnos;


    }

}
