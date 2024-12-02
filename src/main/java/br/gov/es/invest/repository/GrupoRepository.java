package br.gov.es.invest.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.model.Grupo;

public interface GrupoRepository extends Neo4jRepository<Grupo, String> {
    

    @Query("MATCH (grupo:Grupo)\r\n" + //
            "WHERE ($nome IS NULL OR apoc.text.clean(grupo.nome) contains apoc.text.clean($nome) OR apoc.text.clean(grupo.sigla) contains apoc.text.clean($nome))\r\n" + //
            "RETURN grupo SKIP $skip LIMIT $limit")
    public List<Grupo> findAllByFilter(String nome, Pageable pageable);

}
