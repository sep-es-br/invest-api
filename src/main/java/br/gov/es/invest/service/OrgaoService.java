package br.gov.es.invest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.repository.OrgaoRepository;

@Service
public class OrgaoService {
    
    @Autowired
    private OrgaoRepository repository;

    public Orgao findOrCreate(Orgao orgao){

        Orgao probe = new Orgao();
        orgao.setGuid(orgao.getGuid());

        Optional<Orgao> optOrgao = repository.findBy(Example.of(probe), query -> query.first());

        if(optOrgao.isPresent()) {
            return optOrgao.get();
        } else {
            return repository.save(orgao);
        }

    }

}
