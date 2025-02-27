package br.gov.es.invest.service;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;

    public Usuario save(Usuario usuario) {
        repository.getIdBySub(usuario.getSub()).ifPresent(usuario::setId);

        return repository.save(usuario);
    }

    public Usuario getByPapel(Papel papel){
        Usuario userCru = this.repository.getUserCruByPapel(papel.getGuid());

        return userCru == null ? null : this.repository.findById(userCru.getId()).orElse(null);

    } 

    public Optional<Usuario> getUserBySub(String sub){

        Usuario probe = new Usuario();
        probe.setSub(sub);

        Example<Usuario> example = Example.of(probe);

        return this.repository.findBy(example, query -> query.first());

    }


    public Usuario findOrSave(Usuario _usuario) {
        Optional<Usuario> usuarioOpt = repository.findBySub(_usuario.getSub());

        if(usuarioOpt.isPresent()){
            return usuarioOpt.get();
        } else {
            return save(_usuario);
        }
        
    }

    public Usuario findOrSaveWithAvatar(Usuario _usuario) {
        Optional<Usuario> usuarioOpt = this.getUserBySub(_usuario.getSub());

        if(usuarioOpt.isPresent()){
            return usuarioOpt.get();
        } else {
            return save(_usuario);
        }

    }

    public Optional<Usuario> setNewACToken(String sub, String newACToken){
        return repository.setNewACToken(sub, newACToken);
    }

    public List<Usuario> findByGrupo(String grupoId){
        return repository.getByGrupo(grupoId);
    }

}
