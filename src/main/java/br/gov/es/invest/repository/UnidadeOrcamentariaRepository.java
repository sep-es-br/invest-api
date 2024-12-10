package br.gov.es.invest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.UnidadeOrcamentaria;

public interface UnidadeOrcamentariaRepository extends Neo4jRepository<UnidadeOrcamentaria, String> {
    

    @Query("MATCH (unidade:UnidadeOrcamentaria) RETURN unidade ORDER BY unidade.sigla")
    public List<UnidadeOrcamentariaDTOProjection> findAllUnidades();

    @Query("MATCH (unidade:UnidadeOrcamentaria) WHERE unidade.guid = $guid RETURN unidade")
    public Optional<UnidadeOrcamentaria> findByGuid(String guid);

    @Query("MATCH (unidade:UnidadeOrcamentaria)\r\n" + //
            "WHERE elementId(unidade) = $idUnidade\r\n" + //
            "RETURN toString(unidade.codigo)")
    public String getCodById(String idUnidade);
}
