package br.gov.es.invest.repository;

import br.gov.es.invest.dto.grupo.GrupoDoUsuarioListDTO;
import br.gov.es.invest.model.Grupo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface GrupoRepository extends Neo4jRepository<Grupo, Long> {
    
    @Query("""
           MATCH (grupo:Grupo)
           WHERE (
                $nome IS NULL OR 
                apoc.text.clean(grupo.nome) contains apoc.text.clean($nome) OR  
                apoc.text.clean(grupo.sigla) contains apoc.text.clean($nome)
           )
           RETURN grupo 
           SKIP $skip 
           LIMIT $limit
           """)
    public List<Grupo> findAllByFilter(String nome, Pageable pageable);

    @Query("""
           MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(membro)
           WHERE id(grupo) = $grupoId
           RETURN count(DISTINCT membro)
           """)
    public int quantidadeDeMembros(Long grupoId);

    @Query("""
           MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(elemento)
           WHERE id(elemento) = $elementoId
                   AND id(grupo) = $grupoId
           DELETE md
           """)
    public void removerMembro(Long grupoId, Long elementoId);

    @Query("""
           MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)
           WHERE id(modulo) = $moduloId
               AND id(grupo) = $grupoId
           RETURN grupo, collect(pode), collect(modulo)
           """)
    public Optional<Grupo> findByGrupoModulo(Long moduloId, Long grupoId);

    @Query("""
            MATCH (usuario)-[:MEMBRO_DE]->(grupo:Grupo)
            WHERE id(usuario) = $usuarioId
            RETURN id(grupo) as idGrupo, grupo.nome as nome,  grupo.sigla as sigla, grupo.descricao as descricao, usuario.papel as papel
            UNION
            MATCH (usuario)-[:POSSUI]->(papel:Papel)-[:MEMBRO_DE]->(grupo:Grupo)
            WHERE id(usuario) = $usuarioId
            RETURN id(grupo) as idGrupo, grupo.nome as nome, grupo.sigla as sigla, grupo.descricao as descricao, papel.nome as papel
            UNION
            MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(setor:Setor)-[:MEMBRO_DE]->(grupo:Grupo)
            WHERE id(usuario) = $usuarioId
            RETURN id(grupo) as idGrupo, grupo.nome as nome, grupo.sigla as sigla, grupo.descricao as descricao, 'Membro do ' + setor.sigla as papel
            UNION
            MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(:Setor)-[:PERTENCE_A]->(orgao:Orgao)-[:MEMBRO_DE]->(grupo:Grupo)
            WHERE id(usuario) = $usuarioId
            RETURN id(grupo) as idGrupo, grupo.nome as nome, grupo.sigla as sigla, grupo.descricao as descricao, 'Membro de ' + orgao.sigla as papel
            """)
    public List<GrupoDoUsuarioListDTO> getGruposByUsuario(Long usuarioId);

    @Query("""
           MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)
           WHERE id(orgao) = $orgaoId
           RETURN grupo
           """)
    public List<Grupo> getGruposByOrgao(Long orgaoId);

    @Query("""
           MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)
           WHERE id(setor) = $setorId
           RETURN grupo
           UNION 
           MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)
           WHERE id(setor) = $setorId
           RETURN grupo
           """)
    public List<Grupo> getGruposBySetor(Long setorId);

    @Query("""
            MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)
            WHERE id(papel) = $papelId
            RETURN grupo
            UNION
            MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)
            WHERE id(papel) = $papelId
            RETURN grupo
            UNION 
            MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(papel:Papel)
            WHERE id(papel) = $papelId
            RETURN grupo
           """)
    public List<Grupo> getGruposByPapel(Long papelId);


    @Query("""
           MATCH (grupo:Grupo)
           MATCH (entidade)
           WHERE (id(grupo) = $grupoId)
               AND (id(entidade) = $membroId) 
               AND (entidade:Agente OR entidade:Setor OR entidade:Papel OR entidade:Orgao)
           MERGE (entidade)-[:MEMBRO_DE]->(grupo)
           """)
    public void addMembro(Long grupoId, Long membroId);


    @Query("""
           MATCH (g:Grupo)<-[:MEMBRO_DE]-(u:Agente)
           WHERE id(u) = $userId
           RETURN g
           """)
    public List<Grupo> getGrupoMembroDireto(Long userId);
    
    @Query("""
           MATCH (agente:Agente)-[:POSSUI*0..1]->(papel)-[r:MEMBRO_DE]->(g:Grupo)
           WHERE id(agente) = $idAgente
           DELETE r           
           """)
    public void limparGruposDoAgente(Long idAgente);

    

}
