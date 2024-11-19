package br.gov.es.invest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
        return repository.findWithAvatarBySub(sub);
    }

    public Usuario findOrSave(Usuario _usuario) {
        return repository.findBySub(_usuario.getSub()).orElse(save(_usuario));
    }

}
