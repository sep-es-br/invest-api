package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.projection.MembroGrupo;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.ModuloRepository;
import br.gov.es.invest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GrupoService {
    
    private final GrupoRepository repository;

    private final ModuloRepository moduloRepository;

    private final UsuarioRepository usuarioRepository;

    private final Neo4jOperations neo4jOperations;

    private final PapelService papelService;

    

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

    public List<MembroGrupo> getListaMembros(String grupoId) {
        String cypher = "MATCH (orgao:Orgao)-[:MEMBRO_DE]->(g:Grupo)\r\n" + //
                        "WHERE elementId(g) = $grupoId\r\n" + //
                        "RETURN {\r\n" + //
                        "    id: elementId(orgao),\r\n" + //
                        "    icone: 'todos',\r\n" + //
                        "    nomeCompleto: 'Todos',\r\n" + //
                        "    papel: 'Todos',\r\n" + //
                        "    setor: 'Todos',\r\n" + //
                        "    orgao: orgao.sigla + ' - ' + orgao.nome\r\n" + //
                        "} AS membros\r\n" + //
                        "UNION\r\n" + //
                        "MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)-[:MEMBRO_DE]->(g:Grupo)\r\n" + //
                        "WHERE elementId(g) = $grupoId\r\n" + //
                        "RETURN {\r\n" + //
                        "    id: elementId(setor),\r\n" + //
                        "    icone: 'todos',\r\n" + //
                        "    nomeCompleto: 'Todos',\r\n" + //
                        "    papel: 'Todos',\r\n" + //
                        "    setor: setor.sigla,\r\n" + //
                        "    orgao: orgao.sigla + ' - ' + orgao.nome\r\n" + //
                        "} AS membros\r\n" + //
                        "UNION\r\n" + //
                        "MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)-[:MEMBRO_DE]->(g:Grupo),\r\n" + //
                        "        (papel)<-[:POSSUI]-(agente:Agente)\r\n" + //
                        "WHERE elementId(g) = $grupoId\r\n" + //
                        "OPTIONAL MATCH (agente)-[:POSSUI]->(avatar:Avatar)\r\n" + //
                        "RETURN {\r\n" + //
                        "    id: elementId(papel),\r\n" + //
                        "    icone: avatar.blob,\r\n" + //
                        "    nomeCompleto: agente.nomeCompleto,\r\n" + //
                        "    papel: papel.nome,\r\n" + //
                        "    setor: setor.sigla,\r\n" + //
                        "    orgao: orgao.sigla + ' - ' + orgao.nome\r\n" + //
                        "} AS membros\r\n" + //
                        "UNION\r\n" + //
                        "MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:MEMBRO_DE]-(agente:Agente)-[:MEMBRO_DE]->(g:Grupo)\r\n" + //
                        "WHERE elementId(g) = $grupoId\r\n" + //
                        "OPTIONAL MATCH (agente)-[:POSSUI]->(avatar:Avatar)\r\n" + //
                        "RETURN {\r\n" + //
                        "    id: elementId(agente),\r\n" + //
                        "    icone: avatar.blob,\r\n" + //
                        "    nomeCompleto: agente.nomeCompleto,\r\n" + //
                        "    papel: agente.papel,\r\n" + //
                        "    setor: setor.sigla,\r\n" + //
                        "    orgao: orgao.sigla + ' - ' + orgao.nome\r\n" + //
                        "} AS membros";
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("grupoId", grupoId);

        return neo4jOperations.findAll(cypher, params, MembroGrupo.class);
    }

    public Grupo addMembro(Grupo grupo, Orgao orgao, Setor setor, PapelDto papelDto){
        

        if(setor == null) {
            this.repository.addMembro(grupo.getId(), orgao.getId());
        } else if(papelDto == null) {
            this.repository.addMembro(grupo.getId(), setor.getId());
        } else {


            Optional<Papel> papelBanco = papelService.findByGuid(papelDto.guid());
            Papel papelMembro = new Papel();

            if(papelBanco.isPresent()) {
                papelMembro = papelBanco.get();
            } else {

                papelMembro.setGuid(papelDto.guid());
                papelMembro.setNome(papelDto.nome());
                papelMembro.setSetor(setor);

                Optional<Usuario> usuarioBanco = usuarioRepository.findBySub(papelDto.agenteSub());
                Usuario membro = new Usuario();
                ArrayList<Papel> papeisDoUsuario = new ArrayList<>();
    
                if(usuarioBanco.isPresent()){
                    membro = usuarioBanco.get();
                    papeisDoUsuario = new ArrayList<>(membro.getPapeis());
                } else {
                    membro.setSub(papelDto.agenteSub());
                    membro.setNomeCompleto(papelDto.agenteNome());
                    membro.setName(papelDto.agenteNome().split(" ")[0]);
                }

                papeisDoUsuario.add(papelMembro);
                membro.setPapeis(papeisDoUsuario);

                usuarioRepository.save(membro);

            }
            this.repository.addMembro(grupo.getId(), papelMembro.getId());

        }
        
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
