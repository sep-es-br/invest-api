package br.gov.es.invest.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Usuario;

public interface UsuarioRepository extends Neo4jRepository<Usuario, String> {
    
    @Query("MATCH (user:Usuario)-[ac:ATUA_COMO]->(funcao:Funcao) WHERE user.sub = $sub RETURN user, collect(ac), collect(funcao) LIMIT 50")
    public Optional<Usuario> findBySub(String sub);

    @Query("MATCH (user:Usuario)-[ac:ATUA_COMO]->(funcao:Funcao)\r\n" + //
                "OPTIONAL MATCH (avatar:Avatar)<-[p:POSSUI]-(user) " +
         "WHERE user.sub = $sub " +
         "RETURN user, collect(p), collect(avatar), collect(ac), collect(funcao) LIMIT 50")
    public Usuario findWithAvatarBySub(String sub);

    @Query("MATCH (usuario:Usuario)\r\n" + //
                "WHERE usuario.sub = $sub\r\n" + //
                "RETURN elementId(usuario)")
    public Optional<String> getIdBySub(String sub);
}
