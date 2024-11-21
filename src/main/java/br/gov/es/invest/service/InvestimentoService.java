package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.repository.InvestimentoRepository;

@Service
public class InvestimentoService {
    
    @Autowired
    private InvestimentoRepository repository;


    public void saveAll(List<Investimento> investimentos) {
        repository.saveAll(investimentos);
    }

    public List<Investimento> findAllByFilter(
            String nome, String codUnidade, String codPO,
            String exercicio, int numPag, int qtPorPag
        ) {
        return repository.findAllByFilter(nome, codUnidade, codPO, exercicio, (numPag-1)*qtPorPag, qtPorPag);
    }

    public int ammountByFilter(
            String nome, String codUnidade, String codPO, String exercicio
        ){
        return repository.countByFilter(nome, codUnidade, codPO, exercicio);
    }
}
