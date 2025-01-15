package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.AreaTematicaDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class AreaTematica extends Entidade {
    

    private String nome;

    public AreaTematica(AreaTematicaDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
    }

}
