package br.gov.es.invest.repository;

import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.EtapaEnum;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EtapaRepository extends Neo4jRepository<Etapa, Long> {
    
    public Optional<Etapa> findByEtapaId(EtapaEnum etapa);
    
}
