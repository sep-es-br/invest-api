package br.gov.es.invest.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.repository.StatusRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatusService {
    
    private Node statusNode = Cypher.node("Status").named("status");

    private final StatusRepository repository;

    private final Neo4jClient neo4jClient;

    private final ObjetoService objetoService;



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

    public Optional<Status> getByStatusId(String statusId){

        Status statusProbe = new Status();
        statusProbe.setStatusId(StatusEnum.valueOf(statusId));

        return repository.findBy(Example.of(statusProbe), q -> q.first());

    }

    public Status findById(String statusId) {
        return repository.findById(statusId).orElse(null);
    }

    public void aplicarStatus(Objeto objeto, Status status) {
        
        Node objetoNode = Cypher.node("Objeto").named("objeto");

        Statement cypher = Cypher.match(objetoNode)
                                    .where(objetoNode.elementId().eq(Cypher.parameter("objetoId")))
                                    .with(objetoNode)
                                    .match(statusNode)
                                    .where(statusNode.elementId().eq(Cypher.parameter("statusId")))
                                    .with(objetoNode, statusNode)
                                    .where(Cypher.not(Cypher.exists(objetoNode.relationshipTo(statusNode, "EM"))))
                                    .merge(
                                        objetoNode.relationshipTo(statusNode, "EM")
                                        .withProperties(Map.of("timestamp", Cypher.parameter("timestamp"))))
                                    .build();

        Configuration config = Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
        
        Renderer renderer = Renderer.getRenderer(config);

        Logger.getGlobal().log(Level.INFO, "cypher: {0}", new Object[]{renderer.render(cypher)});

        
        neo4jClient.query(renderer.render(cypher))
                    .bindAll(Map.of(
                        "objetoId", objeto.getId(), 
                        "statusId", status.getId(),
                        "timestamp", ZonedDateTime.now()
                    )).run();

    }

}
