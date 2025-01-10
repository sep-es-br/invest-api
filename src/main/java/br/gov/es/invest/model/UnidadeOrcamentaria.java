package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Node
public class UnidadeOrcamentaria extends Entidade implements Serializable {
    
    private String codigo;
    private String guid;
    private String sigla;
    private String nome;

    @Relationship(type = "CONTROLA", direction = Direction.OUTGOING)
    private Set<PlanoOrcamentario> planosOrcamentarios;

    public UnidadeOrcamentaria(UnidadeOrcamentariaDTO dto) {
        this.setId(dto.id());
        this.guid = dto.guid();
        this.codigo = dto.codigo();
        this.nome = dto.nome();
        this.sigla = dto.sigla();
    }

}
