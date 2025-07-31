package br.gov.es.invest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Usuario;

public interface UsuarioRepository extends Neo4jRepository<Usuario, Long> {
    
    @Query("MATCH (usuario:Usuario)\r\n" + //
                "WHERE usuario.sub = $sub\r\n" + //
                "RETURN id(usuario)")
    public Optional<Long> getIdBySub(String sub);

    @Query("MATCH (usuario:Usuario) \r\n" + //
            "WHERE usuario.sub = $sub \r\n" + //
            "SET usuario.ACToken = $newACToken \r\n" + //
            "RETURN usuario")
    public Optional<Usuario> setNewACToken(String sub, String newACToken);
    
    @Query("MATCH (g:Grupo)<-[oldR:MEMBRO_DE]-(u:Usuario)-[:POSSUI]->(papel:Papel)\r\n" + //
                "WHERE id(u) = $userId\r\n" + //
                "    AND id(papel) = $papelId\r\n" + //
                "MERGE (papel)-[:MEMBRO_DE]->(g)\r\n" + //
                "DELETE oldR")
    public void transferirGrupo(Long userId, Long papelId);

}
