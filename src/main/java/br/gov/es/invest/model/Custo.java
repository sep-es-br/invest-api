package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Custo extends Entidade implements Serializable {
     
    private Integer anoExercicio;

    @Relationship(type = "INDICADA_POR", direction = Direction.OUTGOING)
    private Set<IndicadaPor> indicadaPor;

}
