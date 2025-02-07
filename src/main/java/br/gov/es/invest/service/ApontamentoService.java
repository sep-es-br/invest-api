package br.gov.es.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.repository.ApontamentoRepository;

@Service
public class ApontamentoService {
    
    @Autowired
    private ApontamentoRepository apontamentoRepository;

    public Apontamento save(Apontamento apontamento){

        return apontamentoRepository.save(apontamento);

    }  

    public void mergeObjetoApontamento(Apontamento apontamento, Objeto objeto){
        apontamento = apontamentoRepository.save(apontamento);

        apontamentoRepository.mergeObjetoApontamento(objeto.getId(), apontamento.getId());
    }

}
