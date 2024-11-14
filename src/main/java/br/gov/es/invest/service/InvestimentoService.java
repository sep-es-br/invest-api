package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.gov.es.invest.dto.InvestimentoFiltroDTO;
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
