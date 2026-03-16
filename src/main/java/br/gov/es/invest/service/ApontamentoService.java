package br.gov.es.invest.service;

import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.repository.ApontamentoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApontamentoService {
    
    @Autowired
    private ApontamentoRepository apontamentoRepository;

    public Apontamento save(Apontamento apontamento){

        return apontamentoRepository.save(apontamento);

    } 
    
    public void remover(Apontamento apontamento) {
        // apontamentoRepository.delete(apontamento);
        Optional<Apontamento> optApontamento = apontamentoRepository.findById(apontamento.getId());

        optApontamento.ifPresent(_apontamento -> {
            _apontamento.setActive(false);
            apontamentoRepository.save(_apontamento);
        } );

        
    }
    
    public List<Apontamento> findByObjeto(Long idObjeto) {
        List<Long> ids = this.apontamentoRepository.findIdsByObjeto(idObjeto);
        
        return this.apontamentoRepository.findAllById(ids);
    }

}
