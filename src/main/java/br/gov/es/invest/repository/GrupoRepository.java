package br.gov.es.invest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Grupo;

public interface GrupoRepository extends Neo4jRepository<Grupo, String> {
    
    final String GRUPO_HIDRATADO = "WITH grupo\r\n" + //
                "OPTIONAL MATCH (grupo)<-[md:MEMBRO_DE]-(membro:Usuario)-[mds:MEMBRO_DE]->(setor:Setor)-[pa:PERTENCE_A]->(orgao:Orgao)\r\n" + //
                "RETURN grupo, collect(md), collect(membro), collect(mds), collect(setor), collect(pa), collect(orgao)";

    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(grupo.nome) contains apoc.text.clean($nome) OR apoc.text.clean(grupo.sigla) contains apoc.text.clean($nome))\r\n" + //
            "RETURN grupo SKIP $skip LIMIT $limit")
    public List<Grupo> findAllByFilter(String nome, Pageable pageable);

    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE elementId(grupo) = $id\r\n" + //
            GRUPO_HIDRATADO)
    public Optional<Grupo> findByIdHidratado(String id);

    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE elementId(grupo) = $grupoId\r\n" + //
            "OPTIONAL MATCH (grupo)<-[md:MEMBRO_DE]-(membro:Usuario)-[mds:MEMBRO_DE]->(setor:Setor)-[pa:PERTENCE_A]->(orgao:Orgao)\r\n" + //
            "RETURN count(membro)")
    public int quantidadeDeMembros(String grupoId);

    @Query("MATCH (grupo:Grupo)<-[md:MEMBRO_DE]-(usuario:Usuario)\r\n" + //
                "WHERE elementId(grupo) = $grupoId\r\n" + //
                "    AND elementId(usuario) = $usuarioId\r\n" + //
                "DELETE md\r\n" + //
                GRUPO_HIDRATADO)
    public Grupo removerMembro(String grupoId, String usuarioId);

    @Query("MATCH (modulo:Modulo)<-[pode:PODE]-(grupo:Grupo)\r\n" + //
            "WHERE elementId(modulo) = $moduloId\r\n" + //
            "    AND elementId(grupo) = $grupoId\r\n" + //
            "RETURN grupo, collect(pode), collect(modulo)")
    public Optional<Grupo> findByGrupoModulo(String moduloId, String grupoId);

    @Query("MATCH (grupo:Grupo)<-[:MEMBRO_DE]-(usuario:Usuario)\r\n" + //
                "WHERE elementId(usuario) = $usuarioId\r\n" + //
                "RETURN grupo")
    public List<Grupo> getGruposByUsuario(String usuarioId);
}
