package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.AreaTematicaDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@Node
@SuperBuilder
public class AreaTematica extends Entidade {
    

    private String nome;

    public AreaTematica(String nome) {
        this.nome = nome;
    }

    public AreaTematica(AreaTematicaDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
    }

}
