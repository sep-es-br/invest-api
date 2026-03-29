package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@RelationshipProperties
@SuperBuilder
public class Executa extends NoEntidade {
    
    @TargetNode
    private Comando comando;
    
    private String[] args;

}
