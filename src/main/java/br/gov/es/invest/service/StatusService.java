package br.gov.es.invest.service;

import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.repository.StatusRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.HashedMap;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository repository;

    private final Neo4jClient neo4jClient;

    private Node statusNode = Cypher.node("Status").named("status");


    public Status findOrCreate(Status status) {
        
        Status statusProbe = new Status();
        statusProbe.setNome(status.getNome());

        Optional<Status> optStatus = repository.findBy(Example.of(statusProbe), q -> q.first());

        return optStatus.orElse(status);

    }

    public List<Status> findAllStatusObjeto(){
        return repository.findAllStatusObjeto();
    }


    public List<Status> findAll(){
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
    }
    
    public List<Status> findAllForFluxo(){
        return repository.findAllForFluxo();
    }

    public Optional<Status> getByStatusId(String statusId){

        Status statusProbe = new Status();
        statusProbe.setStatusId(StatusEnum.valueOf(statusId));

        return repository.findBy(Example.of(statusProbe), q -> q.first());

    }

    public Status findById(Long statusId) {
        return repository.findById(statusId).orElse(null);
    }

    public void aplicarStatus(Objeto objeto, Status status) {

        Assert.notNull(objeto.getId(), "Objeto não está salvo no Banco");
        Assert.notNull(status.getId(), "Status não está salvo no Banco");
        
        String cypher = """
                MATCH (objeto:Objeto), (status:Status)
                WHERE id(objeto) = $objetoId
                AND id(status) = $statusId

                OPTIONAL MATCH (objeto)-[oldRel:EM]->(:Status)
                DELETE oldRel

                MERGE (objeto)-[rel:EM]->(status)
                SET rel.timestamp = $timestamp
                """;

        Map<String, Object> params = new HashedMap<>();
        params.put("objetoId", objeto.getId());
        params.put("statusId", status.getId());
        params.put("timestamp", ZonedDateTime.now());

        neo4jClient.query(cypher)
                    .bindAll(params)
                    .run();

    }
}
