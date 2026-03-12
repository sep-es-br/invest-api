package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipId;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class RelacionamentoEntidade {

    @RelationshipId @GeneratedValue
    private Long id;

      
    
}
