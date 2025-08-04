package br.gov.es.invest.service;


import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.ResultStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.UsuarioRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository repository;
    
    private final PapelService papelSrv;

    public Usuario save(Usuario usuario) {
        
        Optional<Usuario> usuarioBanco = getUserBySub(usuario.getSub());
        
        if(usuarioBanco.isPresent()){
            Usuario user = usuarioBanco.get();
            usuario.setId(user.getId());
            
            List<String> idsPapeisBanco = Optional.ofNullable(user.getPapeis())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(Papel::getId)
                                            .collect(Collectors.toList());

            List<String> idsPapeisAtualizado = Optional.ofNullable(usuario.getPapeis())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(Papel::getId)
                                            .collect(Collectors.toList());
            
            List<String> papeisRemovidos = idsPapeisBanco.stream()
                                            .filter(p -> !idsPapeisAtualizado.contains(p))
                                            .collect(Collectors.toList());
            
            if (!papeisRemovidos.isEmpty()) {
                this.papelSrv.deleteAllById(papeisRemovidos);
            }
            
        }
        
        return repository.save(usuario);
    } 
    
    public List<Usuario> findAll(){
        return repository.findAll();
    }

    public Optional<Usuario> getUserBySub(String sub){

        Usuario probe = new Usuario();
        probe.setSub(sub);

        Example<Usuario> example = Example.of(probe);

        return this.repository.findBy(example, query -> query.first());

    }
    
    public void atualizarPapeis(String idAgente, List<Papel> papeisAtualizados){
        
        
        
    }

    public void transferirGrupo(String userId, String papelId) {
        repository.transferirGrupo(userId, papelId);
    }

    public Usuario findOrSave(Usuario _usuario) {
        return this.getUserBySub(_usuario.getSub()).orElseGet(() -> save(_usuario));   
    }

}
