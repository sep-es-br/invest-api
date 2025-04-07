package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.repository.PlanoOrcamentarioRepository;

@Service
public class PlanoOrcamentarioService {

    @Autowired
    private PlanoOrcamentarioRepository repository;

    @Autowired
    private PlanoOrcamentarioBIService planoOrcamentarioBIService;

    public void saveAll(List<PlanoOrcamentario> planos) {
        repository.saveAll(planos);
    }

    public List<PlanoOrcamentario> getAllSimples() {
        return repository.getAllSimples();
    }

    public String getCodById(String idPlano) {
        return repository.getCodById(idPlano);
    }

    public void atualizarNomesComBi() {
        List<PlanoOrcamentario> planos = repository.findAll();

        for(PlanoOrcamentario plano : planos){
            
            PlanoOrcamentario planoBi = planoOrcamentarioBIService.getPlanoPorCod(plano.getCodigo());
            if(planoBi == null){
                Logger.getGlobal().severe( String.format("plano cod. %s não encontrado", plano.getCodigo()));
            } else {
                Logger.getGlobal().info( String.format("%s: %s ==> %s", plano.getCodigo(), plano.getNome(), planoBi.getNome()));
                
                plano.setNome(planoBi.getNome());
            }
        }

        repository.saveAll(planos);
    }


    public PlanoOrcamentario findOrCreateByCod(PlanoOrcamentario plano){
        
        PlanoOrcamentario probe = new PlanoOrcamentario();
        probe.setCodigo(plano.getCodigo());
        
        Optional<PlanoOrcamentario> optPlano = repository.findBy( Example.of(probe), query -> query.first());

        return optPlano.orElse(plano);        


    }

}
