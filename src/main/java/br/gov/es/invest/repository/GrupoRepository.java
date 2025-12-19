package br.gov.es.invest.repository;

import br.gov.es.invest.model.Grupo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface GrupoRepository extends Neo4jRepository<Grupo, Long> {
    
    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(grupo.nome) contains apoc.text.clean($nome) OR apoc.text.clean(grupo.sigla) contains apoc.text.clean($nome))\r\n" + //
            "RETURN grupo SKIP $skip LIMIT $limit")
    public List<Grupo> findAllByFilter(String nome, Pageable pageable);

    @Query("MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(membro)\r\n" + //
                "WHERE id(grupo) = $grupoId\r\n" + //
                "RETURN count(membro)")
    public int quantidadeDeMembros(Long grupoId);

    @Query("MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(elemento)\r\n" + //
                "WHERE id(elemento) = $elementoId\r\n" + //
                "        AND id(grupo) = $grupoId\r\n" + //
                "DELETE md")
    public void removerMembro(Long grupoId, Long elementoId);

    @Query("MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)\r\n" + //
            "WHERE id(modulo) = $moduloId\r\n" + //
            "    AND id(grupo) = $grupoId\r\n" + //
            "RETURN grupo, collect(pode), collect(modulo)")
    public Optional<Grupo> findByGrupoModulo(Long moduloId, Long grupoId);

    @Query("MATCH (usuario)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE id(usuario) = $usuarioId\r\n" + //
            "RETURN grupo\r\n" + //
            "UNION\r\n" + //
            "MATCH (usuario)-[:POSSUI]->(:Papel)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE id(usuario) = $usuarioId\r\n" + //
            "RETURN grupo\r\n" + //
            "UNION\r\n" + //
            "MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(:Setor)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE id(usuario) = $usuarioId\r\n" + //
            "RETURN grupo\r\n" + //
            "UNION\r\n" + //
            "MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(:Setor)-[:PERTENCE_A]->(:Orgao)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE id(usuario) = $usuarioId\r\n" + //
            "RETURN grupo")
    public List<Grupo> getGruposByUsuario(Long usuarioId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)\r\n" + //
                "WHERE id(orgao) = $orgaoId\r\n" + //
                "RETURN grupo")
    public List<Grupo> getGruposByOrgao(Long orgaoId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)\r\n" + //
                "WHERE id(setor) = $setorId\r\n" + //
                "RETURN grupo\r\n" + //
                "UNION \r\n" + //
                "MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)\r\n" + //
                "WHERE id(setor) = $setorId\r\n" + //
                "RETURN grupo")
    public List<Grupo> getGruposBySetor(Long setorId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)\r\n" + //
                "    WHERE id(papel) = $papelId\r\n" + //
                "    RETURN grupo\r\n" + //
                "    UNION \r\n" + //
                "    MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)\r\n" + //
                "    WHERE id(papel) = $papelId\r\n" + //
                "    RETURN grupo\r\n" + //
                "    UNION \r\n" + //
                "    MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(papel:Papel)\r\n" + //
                "    WHERE id(papel) = $papelId\r\n" + //
                "    RETURN grupo")
    public List<Grupo> getGruposByPapel(Long papelId);


    @Query("MATCH (grupo:Grupo)\r\n" + //
                "MATCH (entidade)\r\n" + //
                "WHERE (id(grupo) = $grupoId)\r\n" + //
                "    AND (id(entidade) = $membroId) \r\n" + //
                "    AND (entidade:Agente OR entidade:Setor OR entidade:Papel OR entidade:Orgao)\r\n" + //

                "MERGE (entidade)-[:MEMBRO_DE]->(grupo)")
    public void addMembro(Long grupoId, Long membroId);


    @Query("MATCH (g:Grupo)<-[:MEMBRO_DE]-(u:Agente)\r\n" + //
                "WHERE id(u) = $userId\r\n" + //
                "RETURN g")
    public List<Grupo> getGrupoMembroDireto(Long userId);
    
    @Query("""
           MATCH (agente:Agente)-[:POSSUI*0..1]->(papel)-[r:MEMBRO_DE]->(g:Grupo)
           WHERE id(agente) = $idAgente
           DELETE r           
           """)
    public void limparGruposDoAgente(Long idAgente);

    

}
