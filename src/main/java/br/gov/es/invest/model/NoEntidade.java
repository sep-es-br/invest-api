package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class NoEntidade {

    @Id @GeneratedValue
    private Long id;

      
    
}
