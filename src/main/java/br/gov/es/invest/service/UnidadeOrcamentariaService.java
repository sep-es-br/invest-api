package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.UnidadeOrcamentariaRepository;

@Service
public class UnidadeOrcamentariaService {
    
    @Autowired
    private UnidadeOrcamentariaRepository repository;

    public void saveAll(List<UnidadeOrcamentaria> unidades) {
        repository.saveAll(unidades);
    }

    public List<UnidadeOrcamentaria> getAll() {
        return repository.findAll();
    }

    public List<UnidadeOrcamentariaDTOProjection> getAllSimples() {
        return repository.findAllUnidades();
    }

    public String getCodById(String idUnidade) {
        return repository.getCodById(idUnidade);
    }

    public UnidadeOrcamentaria findOrCreateByCod(UnidadeOrcamentaria unidade){
        
        UnidadeOrcamentaria probe = new UnidadeOrcamentaria();
        probe.setCodigo(unidade.getCodigo());

        Optional<UnidadeOrcamentaria> optUnidade = repository.findBy(Example.of(probe), query -> query.first());

        return optUnidade.orElse(unidade);
    }

    public List<UnidadeOrcamentaria> findByOrgaoId(Orgao orgao) {
        Orgao orgaoProbe = new Orgao();
        orgaoProbe.setId(orgao.getId());

        UnidadeOrcamentaria probe = new UnidadeOrcamentaria();
        probe.setOrgaoPai(orgaoProbe);

        return this.repository.findBy(Example.of(probe), q -> q.all());
    }


}
