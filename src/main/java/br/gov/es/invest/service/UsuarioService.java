package br.gov.es.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;

    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario getUserWithAvatarBySub(String sub){
        return repository.findWithAvatarBySub(sub);
    }

    public Usuario findOrSave(Usuario _usuario) {
        Usuario usuario = repository.findBySub(_usuario.getSub());

        return usuario == null ? repository.save(_usuario) : usuario;

    }

}
