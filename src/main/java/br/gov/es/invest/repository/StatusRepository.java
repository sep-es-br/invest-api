package br.gov.es.invest.repository;

import br.gov.es.invest.model.Status;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface StatusRepository extends Neo4jRepository<Status, Long> {
    

    @Query("""
            MATCH (status:Status)
            WITH status, 
                 CASE 
                      WHEN status.statusId = 'SOLICITADO' THEN 0
                      WHEN status.statusId = 'EM_ANALISE' THEN 1
                      WHEN status.statusId = 'EM_APROVACAO' THEN 2
                      WHEN status.statusId = 'FINALIZANDO' THEN 3
                      WHEN status.statusId = 'DEVOLVIDO' THEN 4 
                      WHEN status.statusId = 'CADASTRADO' THEN 5
                      ELSE -2 END AS ordem
            RETURN status
            ORDER BY ordem
           """)
    public List<Status> findAllStatusObjeto();
    
    @Query("""
            MATCH (status:Status)
            WHERE status.statusId <> 'CADASTRADO'
            WITH status, 
                 CASE 
                      WHEN status.statusId = 'SOLICITADO' THEN 0
                      WHEN status.statusId = 'EM_ANALISE' THEN 1
                      WHEN status.statusId = 'EM_APROVACAO' THEN 2
                      WHEN status.statusId = 'FINALIZANDO' THEN 3
                      WHEN status.statusId = 'DEVOLVIDO' THEN 4 
                      ELSE -2 END AS ordem
            RETURN status
            ORDER BY ordem
           """)
    public List<Status> findAllForFluxo();

}
