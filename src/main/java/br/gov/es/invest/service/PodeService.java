package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Pode;
import br.gov.es.invest.repository.GrupoRepository;

@Service
public class PodeService {
    
    @Autowired
    private GrupoRepository grupoRepository;

    public Pode findByGrupoModulo(String moduloId, String grupoId){

        Optional<Grupo> optGrupo = grupoRepository.findByGrupoModulo(moduloId, grupoId);

        if(optGrupo.isPresent()) {
            return new ArrayList<>(optGrupo.get().getPermissoes()).get(0) ;
        } 

        return null;
    }



}
