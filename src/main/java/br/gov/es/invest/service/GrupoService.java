package br.gov.es.invest.service;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.projection.MembroGrupo;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.ModuloRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrupoService {
    
    private final GrupoRepository repository;


    private final UsuarioService usuarioService;
    private final ModuloRepository moduloRepository;

    private final Neo4jOperations neo4jOperations;

    private final PapelService papelService;
    private final UsuarioService usuarioSrv;

    
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

    public Optional<Grupo> findById(Long id){

        return repository.findById(id);
    }

    public Grupo save(Grupo grupo){
        return repository.save(grupo);
    }

    public Grupo delete (Long grupoId){
        Optional<Grupo> optGrupo = repository.findById(grupoId);
        
        return optGrupo.map(grupo -> {
            repository.delete(grupo);
            return grupo;
        }).orElse(null);
    }


    public List<MembroGrupo> getListaMembros(Long grupoId) {
        String cypher = """
            MATCH (orgao:Orgao)-[:MEMBRO_DE]->(g:Grupo)
            WHERE id(g) = $grupoId
            RETURN {
                id: id(orgao),
                icone: 'todos',
                nomeCompleto: 'Todos',
                papel: 'Todos',
                setor: 'Todos',
                orgao: orgao.sigla + ' - ' + orgao.nome
            } AS membros
            UNION
            MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)-[:MEMBRO_DE]->(g:Grupo)
            WHERE id(g) = $grupoId
            RETURN {
                id: id(setor),
                icone: 'todos',
                nomeCompleto: 'Todos',
                papel: 'Todos',
                setor: setor.sigla,
                orgao: orgao.sigla + ' - ' + orgao.nome
            } AS membros
            UNION
            MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)-[:MEMBRO_DE]->(g:Grupo),
                    (papel)<-[:POSSUI]-(agente:Agente)
            WHERE id(g) = $grupoId AND agente.deletadoEm IS NULL
            OPTIONAL MATCH (agente)-[:POSSUI]->(avatar:Avatar)
            RETURN {
                id: id(papel),
                icone: avatar.blob,
                nomeCompleto: agente.nomeCompleto,
                papel: papel.nome,
                setor: setor.sigla,
                orgao: orgao.sigla + ' - ' + orgao.nome
            } AS membros
            UNION
            MATCH (orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:MEMBRO_DE]-(agente:Agente)-[:MEMBRO_DE]->(g:Grupo)
            WHERE id(g) = $grupoId AND agente.deletadoEm IS NULL
            OPTIONAL MATCH (agente)-[:POSSUI]->(avatar:Avatar)
            RETURN {
                id: id(agente),
                icone: avatar.blob,
                nomeCompleto: agente.nomeCompleto,
                papel: agente.papel,
                setor: setor.sigla,
                orgao: orgao.sigla + ' - ' + orgao.nome
            } AS membros
             """;
        
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

                Optional<Agente> usuarioBanco = usuarioService.getUserBySub(papelDto.agenteSub());
                Agente membro = new Agente();
                ArrayList<Papel> papeisDoUsuario = new ArrayList<>();
    
                if(usuarioBanco.isPresent()){
                    membro = usuarioBanco.get();
                    membro.setDeletadoEm(null);
                    papeisDoUsuario = new ArrayList<>(membro.getPapeis());
                } else {
                    membro.setSub(papelDto.agenteSub());
                    membro.setNomeCompleto(papelDto.agenteNome());
                    membro.setName(papelDto.agenteNome().split(" ")[0]);
                }
                
                papeisDoUsuario.add(papelMembro);
                membro.setPapeis(papeisDoUsuario);

                this.usuarioSrv.save(membro);

            }
            this.repository.addMembro(grupo.getId(), papelMembro.getId());

        }
        
        return this.repository.findById(grupo.getId()).get();
    }

    public int quantidadeDeMembros(Long grupoId){
        this.papelService.limparLixo();
        return this.repository.quantidadeDeMembros(grupoId);
    }

    public Grupo removerMembro(Long grupoId, Long membroId){
        this.repository.removerMembro(grupoId, membroId);
        
        return this.repository.findById(grupoId).orElse(null);
    }

    public List<Grupo> getGruposDoUsuario(Long usuarioId) {

        return this.repository.getGruposByUsuario(usuarioId);
    }

    public List<Grupo> getGruposByOrgao(Long orgaoId){
        return this.repository.getGruposByOrgao(orgaoId);
    }

    public List<Grupo> getGruposBySetor(Long orgaoId){
        return this.repository.getGruposBySetor(orgaoId);
    }

    public List<Grupo> getGruposByPapel(Long papelId){
        return this.repository.getGruposByPapel(papelId);
    }
    
    public void limparGruposDoAgente(Long idAgente){
        this.repository.limparGruposDoAgente(idAgente);
    }

}
