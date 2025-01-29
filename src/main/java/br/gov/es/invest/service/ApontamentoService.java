package br.gov.es.invest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.repository.ApontamentoRepository;

@Service
public class ApontamentoService {
    
    private ApontamentoRepository apontamentoRepository;

    @Transactional
    public void adicionarApontamento(Objeto objeto, Apontamento apontamento){

        apontamento = apontamentoRepository.save(apontamento);

        apontamentoRepository.mergeObjetoApontamento(objeto, apontamento);

    }   

}
