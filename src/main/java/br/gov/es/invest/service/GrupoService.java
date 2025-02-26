package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.ModuloRepository;
import br.gov.es.invest.repository.UsuarioRepository;

@Service
public class GrupoService {
    
    @Autowired
    private GrupoRepository repository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        Grupo deletedGrupo = repository.findById(grupoId).orElse(null);

        if(deletedGrupo != null) {
            repository.delete(deletedGrupo);
        }

        return deletedGrupo;
    }

    public Grupo addMembro(Grupo grupo, Orgao orgao, Setor setor, PapelDto papelDto){
        
      
        Optional<Usuario> usuarioBanco = usuarioRepository.findBySub(papelDto.agenteSub());
        Usuario membro = new Usuario();

        if(usuarioBanco.isPresent()){
            membro = usuarioBanco.get();
        } else {
            membro.setSub(papelDto.agenteSub());
            membro.setNomeCompleto(papelDto.agenteNome());
            membro.setName(papelDto.agenteNome().split(" ")[0]);
        }
        
        membro.setPapel(papelDto.nome());
        membro.setSetor(setor);
        membro = usuarioRepository.save(membro);
    
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
        return this.repository.getGruposByUsuario(usuarioId);
    }

}
