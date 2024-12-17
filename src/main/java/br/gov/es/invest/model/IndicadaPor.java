package br.gov.es.invest.model;

import java.lang.annotation.Target;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@RelationshipProperties
@NoArgsConstructor
public class IndicadaPor extends Entidade {
    
    @TargetNode
    private FonteOrcamentaria fonteOrcamentaria;

    private double previsto;
    private double contratado;


}
