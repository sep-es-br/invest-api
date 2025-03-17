package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoService {
    
    private final GrupoRepository repository;

    private final ModuloRepository moduloRepository;

    private final UsuarioService usuarioService;

    private final Neo4jClient neo4jClient;

    public List<Grupo> findAll(String nome, Pageable pageable) {
        
        return repository.findAllByFilter(nome, pageable);

    }

    public List<Grupo> findAll(String nome) {
        
        Grupo grupoProbe = new Grupo();

        ExampleMatcher matcher = ExampleMatcher.matchingAny();

        if(nome != null){
            grupoProbe.setSigla(nome);
            grupoProbe.setNome(nome);

            matcher = matcher.withMatcher("sigla", GenericPropertyMatchers.ignoreCase().contains())
                                .withMatcher("nome", GenericPropertyMatchers.ignoreCase().contains());
        }


        return repository.findAll(Example.of(grupoProbe, matcher), Sort.by("nome"));

    }

    public Optional<Grupo> findById(String id){

        return repository.findById(id);
    }

    public Grupo save(Grupo grupo){
        return repository.save(grupo);
    }

    public Grupo delete (String grupoId){
        Optional<Grupo> optGrupo = repository.findById(grupoId);
        
        return optGrupo.map(grupo -> {
            repository.delete(grupo);
            return grupo;
        }).orElse(null);
    }


    public Grupo addMembro(Grupo grupo, Orgao orgao, Setor setor, PapelDto papelDto){
        
      
        Optional<Usuario> usuarioBanco = usuarioService.getUserBySub(papelDto.agenteSub());

        Usuario membro = usuarioBanco.orElseGet(
            () -> {
                Usuario _membro = new Usuario();
                _membro.setSub(papelDto.agenteSub());
                _membro.setNomeCompleto(papelDto.agenteNome());
                _membro.setName(papelDto.agenteNome().split(" ")[0]);
                return _membro;
            }
        );
        
        membro.setPapel(papelDto.nome());
        membro.setSetor(setor);
        membro = usuarioService.save(membro);
    
        this.repository.addMembro(membro.getId(), grupo.getId());
    
        return this.repository.findById(grupo.getId()).get();
    }

    public int quantidadeDeMembros(String grupoId){
        return this.repository.quantidadeDeMembros(grupoId);
    }

    public Grupo removerMembro(String grupoId, String membroId){
        this.repository.removerMembro(grupoId, membroId);
        
        return this.repository.findById(grupoId).orElse(null);
    }

    public List<Grupo> getGruposDoUsuario(String usuarioId) {


        // MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(usuario:Usuario)\r\n" + //
        //         "WHERE elementId(usuario) = $usuarioId\r\n" + //
        //         "RETURN grupo 

        return this.repository.getGruposByUsuario(usuarioId);
    }

}
