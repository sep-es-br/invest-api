package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.PlanoOrcamentarioRepository;

@Service
public class PlanoOrcamentarioService {

    @Autowired
    private PlanoOrcamentarioRepository repository;

    public void saveAll(List<PlanoOrcamentario> planos) {
        repository.saveAll(planos);
    }

    public List<PlanoOrcamentario> getAllSimples() {
        return repository.getAllSimples();
    }

    public String getCodById(String idPlano) {
        return repository.getCodById(idPlano);
    }

    public PlanoOrcamentario findOrCreateByCod(PlanoOrcamentario plano, UnidadeOrcamentaria unidade){
        
        PlanoOrcamentario probe = new PlanoOrcamentario();
        probe.setCodigo(plano.getCodigo());
        
        Optional<PlanoOrcamentario> optPlano = repository.findBy( Example.of(probe), query -> query.first());

        if(optPlano.isPresent()) {
            return optPlano.get();
        } else {
            HashSet<PlanoOrcamentario> todosPlanosDaUnidade = new HashSet<>(unidade.getPlanosOrcamentarios());
            todosPlanosDaUnidade.add(plano);
            unidade.setPlanosOrcamentarios(todosPlanosDaUnidade);

            return plano;
        }
        


    }

}
