package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entidade {

    @RelationshipId @GeneratedValue
    private String id;

      
    
}
