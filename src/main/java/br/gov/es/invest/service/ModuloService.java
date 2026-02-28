package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
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

    public boolean temAcessoPorDescedencia(Long grupoId, Long moduloId) {
        return repository.temAcessoPorDescedencia(grupoId, moduloId);
    }

    public boolean checarAcesso(Long grupoId, String path){
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

    public boolean checarAcessoUsuario(String path, List<Papel> papeis){

        
        for(Papel papel : papeis){
            if(papel.getId() != null) {
                for(Grupo grupo : grupoRepository.getGruposByPapel(papel.getId())){
                    if(this.checarAcesso(grupo.getId(), path))
                        return true;
                }
            } else if(papel.getSetor() != null && papel.getPrioritario()) {
                Setor setor = papel.getSetor();
                
                if(setor.getId() != null){
                    for(Grupo grupo : grupoRepository.getGruposBySetor(setor.getId())){
                        if(this.checarAcesso(grupo.getId(), path))
                            return true;
                    }
                } else if(setor.getOrgao() != null) {
                    
                    Orgao orgao = setor.getOrgao();
                    
                    if(orgao.getId() != null){
                        for(Grupo grupo : grupoRepository.getGruposByOrgao(orgao.getId())){
                            if(this.checarAcesso(grupo.getId(), path))
                                return true;
                        }
                    }
                    
                }
                                
            }
            
        }
        
        return false;
        
    }

    public List<String> montarCaminhoDeModulo(Modulo modulo, String parentPath, List<String> listaCaminhos) {
        List<Modulo> modulos = repository.findAll();

        ArrayList<String> caminhos = new ArrayList<>();


        return Arrays.asList();

    }

}
