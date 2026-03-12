package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.CustoDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Custo extends NoEntidade implements Serializable {
     
    private Integer anoExercicio;
    
    @Relationship(type = "ESTIMADO", direction = Direction.OUTGOING)
    private Objeto objeto;

    @Relationship(type = "INDICADA_POR", direction = Direction.OUTGOING)
    private List<IndicadaPor> indicadaPor = new ArrayList<>();

    public Custo(CustoDTO dto) {
        this.setId(dto.id());
        this.anoExercicio = dto.anoExercicio();
        this.indicadaPor = dto.indicadaPor().stream().map(IndicadaPor::parse).collect(Collectors.toList());
    }

}
