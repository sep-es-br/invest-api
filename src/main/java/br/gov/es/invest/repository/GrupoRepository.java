package br.gov.es.invest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Grupo;

public interface GrupoRepository extends Neo4jRepository<Grupo, String> {
    
    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(grupo.nome) contains apoc.text.clean($nome) OR apoc.text.clean(grupo.sigla) contains apoc.text.clean($nome))\r\n" + //
            "RETURN grupo SKIP $skip LIMIT $limit")
    public List<Grupo> findAllByFilter(String nome, Pageable pageable);

    @Query("MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(membro)\r\n" + //
                "WHERE elementId(grupo) = $grupoId\r\n" + //
                "RETURN count(membro)")
    public int quantidadeDeMembros(String grupoId);

    @Query("MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(elemento)\r\n" + //
                "WHERE elementId(elemento) = $elementoId\r\n" + //
                "        AND elementId(grupo) = $grupoId\r\n" + //
                "DELETE md")
    public void removerMembro(String grupoId, String elementoId);

    @Query("MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)\r\n" + //
            "WHERE elementId(modulo) = $moduloId\r\n" + //
            "    AND elementId(grupo) = $grupoId\r\n" + //
            "RETURN grupo, collect(pode), collect(modulo)")
    public Optional<Grupo> findByGrupoModulo(String moduloId, String grupoId);

    @Query("MATCH (usuario)-[:POSSUI]->(:Papel)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE elementId(usuario) = $usuarioId\r\n" + //
            "RETURN grupo\r\n" + //
            "UNION\r\n" + //
            "MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(:Setor)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE elementId(usuario) = $usuarioId\r\n" + //
            "RETURN grupo\r\n" + //
            "UNION\r\n" + //
            "MATCH (usuario)-[:POSSUI]->(:Papel)-[:ATUA_EM]->(:Setor)-[:PERTENCE_A]->(:Orgao)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
            "WHERE elementId(usuario) = $usuarioId\r\n" + //
            "RETURN grupo")
    public List<Grupo> getGruposByUsuario(String usuarioId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)\r\n" + //
                "WHERE elementId(orgao) = $orgaoId\r\n" + //
                "RETURN grupo")
    public List<Grupo> getGruposByOrgao(String orgaoId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)\r\n" + //
                "WHERE elementId(setor) = $setorId\r\n" + //
                "RETURN grupo\r\n" + //
                "UNION \r\n" + //
                "MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)\r\n" + //
                "WHERE elementId(setor) = $setorId\r\n" + //
                "RETURN grupo")
    public List<Grupo> getGruposBySetor(String setorId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(orgao:Orgao)<-[:PERTENCE_A]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)\r\n" + //
                "    WHERE elementId(papel) = $papelId\r\n" + //
                "    RETURN grupo\r\n" + //
                "    UNION \r\n" + //
                "    MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(setor:Setor)<-[:ATUA_EM]-(papel:Papel)\r\n" + //
                "    WHERE elementId(papel) = $papelId\r\n" + //
                "    RETURN grupo\r\n" + //
                "    UNION \r\n" + //
                "    MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(papel:Papel)\r\n" + //
                "    WHERE elementId(papel) = $papelId\r\n" + //
                "    RETURN grupo")
    public List<Grupo> getGruposByPapel(String papelId);


    @Query("MATCH (grupo:Grupo)\r\n" + //
                "MATCH (entidade)\r\n" + //
                "WHERE (elementId(grupo) = $grupoId)\r\n" + //
                "    AND (elementId(entidade) = $membroId) \r\n" + //
                "    AND (entidade:Agente OR entidade:Setor OR entidade:Papel OR entidade:Orgao)\r\n" + //
                "MERGE (entidade)-[:MEMBRO_DE]->(grupo)")
    public void addMembro(String grupoId, String membroId);



    

}
