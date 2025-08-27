package br.gov.es.invest.service;


import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.UsuarioDto;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository repository;
    
    public Usuario save(Usuario usuario) {
        
        Optional<Usuario> usuarioBanco = getUserBySub(usuario.getSub());
        usuario.setId(usuarioBanco.map(Usuario::getId).orElse(null));
        
        return repository.save(usuario);
    } 
    
    public Usuario save(UsuarioDto usuario) {
        
        return repository.save(
                getUserBySub(usuario.sub())
                .map(user -> {
                    user.setName(usuario.name());
                    user.setNomeCompleto(usuario.nomeCompleto());
                    user.setTelefone(usuario.telefone());
                    Optional.ofNullable(user.getImgPerfil()).ifPresent(avatarUser -> 
                            avatarUser.setBlob(Optional.ofNullable(usuario.imgPerfil()).map(AvatarDTO::blob).orElse(null)) );
                    user.setEmail(usuario.email());
                    
                    return user;
                
                }).orElseGet(() -> Usuario.parse(usuario))
        );
    } 
    
    public List<Usuario> findAll(){
        return repository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Usuario> getUserBySub(String sub){

        Usuario probe = new Usuario();
        probe.setSub(sub);

        Example<Usuario> example = Example.of(probe);

        return this.repository.findBy(example, query -> query.first());

    }
    

}
