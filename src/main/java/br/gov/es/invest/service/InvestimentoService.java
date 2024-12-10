package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
            String exercicio, String idFonte, Pageable pageable
        ) {
        return repository.findAllByFilter(nome, codUnidade, codPO, exercicio, idFonte, pageable);
    }

    public int ammountByFilter(
            String nome, String codUnidade, String codPO, String exercicio, String idFonte
        ){
        return repository.countByFilter(nome, codUnidade, codPO, exercicio, idFonte);
    }

    public Investimento getByCodUoPo(String codUo, String codPo) {
        return repository.getBycodUoPo(Long.valueOf(codUo), Long.valueOf(codPo));
    }
}
