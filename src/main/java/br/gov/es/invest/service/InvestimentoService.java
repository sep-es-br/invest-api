package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Investimento> findAllByFilter(InvestimentoFiltroDTO filtroDTO) {
        return repository.findAllByFilter(filtroDTO.getNome(), filtroDTO.getExercicio(), filtroDTO.getCodPO(), filtroDTO.getCodUnidade());
    }
}
