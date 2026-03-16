package br.gov.es.invest.repository;

import br.gov.es.invest.model.Agente;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface UsuarioRepository extends Neo4jRepository<Agente, Long> {
    
    @Query("""
           MATCH (usuario:Agente)
           WHERE usuario.sub = $sub
           RETURN id(usuario)
           """)
    public Optional<Long> getIdBySub(String sub);

    @Query("""
           MATCH (usuario:Agente) 
           WHERE usuario.sub = $sub 
           SET usuario.ACToken = $newACToken 
           RETURN usuario
           """)
    public Optional<Agente> setNewACToken(String sub, String newACToken);
    

    @Query("""
           MATCH (g:Grupo)<-[oldR:MEMBRO_DE]-(u:Agente)-[:POSSUI]->(papel:Papel)
           WHERE id(u) = $userId
               AND id(papel) = $papelId
           MERGE (papel)-[:MEMBRO_DE]->(g)
           DELETE oldR 
           """)
    public void transferirGrupo(Long userId, Long papelId);
    
    public Optional<Agente> findBySub(String sub);
    
    final String findAgentesSimples_base = """
                                            MATCH (a:Agente)
                                            WHERE ($term IS NULL OR apoc.text.clean(a.nomeCompleto) CONTAINS apoc.text.clean($term))
                                                AND a.deletadoEm IS NULL
                                            OPTIONAL MATCH (a)-[r:POSSUI]->(avatar:Avatar)
                                           
                                            """;
    
    @Query(
            value = findAgentesSimples_base + """
                                              RETURN a, r, avatar 
                                              ORDER BY apoc.text.clean(a.nomeCompleto) ASC 
                                              SKIP $skip 
                                              LIMIT $limit
                                              """,
            countQuery = findAgentesSimples_base + """
                                                    RETURN count(DISTINCT a)
                                                   """
    )
    public Page<Agente> findAgentesSimples(String term, Pageable pgRequest);

}
