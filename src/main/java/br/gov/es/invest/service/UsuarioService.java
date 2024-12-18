package br.gov.es.invest.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

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

    public Usuario getUserBySub(String sub){
        return repository.findBySub(sub).orElse(null);
    }

    public Usuario getUserWithAvatarBySub(String sub){
        Usuario probe = new Usuario();
        probe.setSub(sub);

        Example<Usuario> example = Example.of(probe);

        Usuario usuario = this.repository.findBy(example, query -> query.firstValue());

        return usuario;
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
        Optional<Usuario> usuarioOpt = repository.findWithAvatarBySub(_usuario.getSub());

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
