package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.CustoDTO;
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

    public Custo(CustoDTO dto) {
        this.setId(dto.getId());
        this.anoExercicio = dto.getAnoExercicio();
        this.indicadaPor = dto.getIndicadaPor().stream().map(indicadaPorDto -> new IndicadaPor(indicadaPorDto)).collect(Collectors.toSet());
    }

}
