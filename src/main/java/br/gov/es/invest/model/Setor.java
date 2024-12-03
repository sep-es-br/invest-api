package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.SetorDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Setor extends Entidade {
    private String guid;
    private String nome;
    private String sigla;

    @Relationship(type = "PERTENCE_A")
    private Orgao orgao;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private List<Usuario> membros;

    public Setor(SetorDto dto) {
        this.setId(dto.getId());
        this.guid = dto.getGuid();
        this.nome = dto.getNome();
        this.sigla = dto.getSigla();
    }
}
