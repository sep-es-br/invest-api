package br.gov.es.invest.service;


import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.usuario.SalvarUsuarioForm;
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
    
    public Usuario save(SalvarUsuarioForm slvUserForm) {
        
        Usuario usuarioBanco = getUserBySub(slvUserForm.sub())
                                .orElseThrow(() -> new RuntimeException("Usuario com sub " + slvUserForm + " não encontrado"));
        
        
        usuarioBanco.set(slvUserForm);
        
        return this.save(usuarioBanco);
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
