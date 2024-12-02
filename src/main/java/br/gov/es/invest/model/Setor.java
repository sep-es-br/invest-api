package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Setor extends MembroGrupo {
    private String nome;

    @Relationship(type = "PERTENCE_A")
    private UnidadeOrcamentaria unidade;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private List<Usuario> membros;
}
