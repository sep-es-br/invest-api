package br.gov.es.invest.service;


import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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

    public Optional<Usuario> getUserBySub(String sub){

        Usuario probe = new Usuario();
        probe.setSub(sub);

        Example<Usuario> example = Example.of(probe);

        return this.repository.findBy(example, query -> query.first());

    }

    public void trasnferirNovoFormato(Usuario usuario, Papel papel){
        if( usuario.getPapeis() != null ){
            Logger.getGlobal().log(Level.SEVERE, "user \"{0}\" usuario já está no novo formato", usuario.getId());
            if(!usuario.getPapeis().isEmpty())
                return;
        }

        usuario.setPapeis(Arrays.asList(papel));

        usuario = this.save(usuario);

        repository.transferirGrupo(usuario.getId(), usuario.getPapeis().get(0).getId());

    }

}
