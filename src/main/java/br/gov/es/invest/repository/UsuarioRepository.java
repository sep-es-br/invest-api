package br.gov.es.invest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Usuario;

public interface UsuarioRepository extends Neo4jRepository<Usuario, String> {
    
    @Query("MATCH (usuario:Usuario)\r\n" + //
                "WHERE usuario.sub = $sub\r\n" + //
                "RETURN elementId(usuario)")
    public Optional<String> getIdBySub(String sub);

    @Query("MATCH (usuario:Usuario) \r\n" + //
            "WHERE usuario.sub = $sub \r\n" + //
            "SET usuario.ACToken = $newACToken \r\n" + //
            "RETURN usuario")
    public Optional<Usuario> setNewACToken(String sub, String newACToken);

    @Query("MATCH (usuario:Usuario)-[:MEMBRO_DE]->(grupo:Grupo)\r\n" + //
                "WHERE elementId(grupo) = $grupoId\r\n" + //
                "OPTIONAL MATCH (usuario)-[possui:POSSUI]->(avatar:Avatar)\r\n" + //
                "RETURN usuario, collect(possui), collect(avatar)")
    public List<Usuario> getByGrupo(String grupoId);

    
    @Query("MATCH (u:Usuario)-[:POSSUI]->(p:Papel)\r\n" + //
                "WHERE p.guid = $guidPapel\r\n" + //
                "RETURN u LIMIT 1")
    public Usuario getUserCruByPapel(String guidPapel);

    @Query("MATCH (g:Grupo)<-[oldR:MEMBRO_DE]-(u:Usuario)-[:POSSUI]->(papel:Papel)\r\n" + //
                "WHERE elementId(u) = $userId\r\n" + //
                "    AND elementId(papel) = $papelId\r\n" + //
                "MERGE (papel)-[:MEMBRO_DE]->(g)\r\n" + //
                "DELETE oldR")
    public void transferirGrupo(String userId, String papelId);

}
