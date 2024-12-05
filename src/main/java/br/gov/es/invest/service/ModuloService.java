package br.gov.es.invest.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.ModuloRepository;

@Service
public class ModuloService {
    
    @Autowired
    private ModuloRepository repository;

    @Autowired
    private GrupoRepository grupoRepository;


    public Set<Modulo> findAll(){


        return new HashSet<>(repository.findAll());
    }

    public Modulo findByPathId(String pathId){
        return repository.findByPathId(pathId);
    }

    public boolean temAcessoPorDescedencia(String grupoId, String moduloId) {
        return repository.temAcessoPorDescedencia(grupoId, moduloId);
    }

    public boolean checarAcesso(String grupoId, String path){
        Modulo modulo = findByPathId(path);

        if(modulo == null) {
            return true;
        }

        Optional<Grupo> grupoComPermissao = grupoRepository.findByGrupoModulo(modulo.getId(), grupoId);
        
        if(grupoComPermissao.isPresent()) {
            return true;
        }

        return temAcessoPorDescedencia(grupoId, modulo.getId());
        
    }

    public boolean checarAcessoUsuario(String path, String userId){
        
        for(Grupo grupo : grupoRepository.getGruposByUsuario(userId)){
            if(this.checarAcesso(grupo.getId(), path))
                return true;
        }

        return false;
        
    }

}
