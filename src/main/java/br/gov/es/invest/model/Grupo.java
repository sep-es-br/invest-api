package br.gov.es.invest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.GrupoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Grupo extends Entidade {
    private String sigla;
    private String icone;
    private String nome;
    private String descricao;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private List<Usuario> membros = Arrays.asList(); 

    public Grupo(GrupoDTO dto){
        this.setId(dto.getId());
        this.sigla = dto.getSigla();
        this.icone = dto.getIcone();
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();

        this.membros = dto.getMembros().stream().map(membroDto -> new Usuario(membroDto)).toList();
    }

    public void addMembro(Usuario membro) {
        ArrayList<Usuario> membrosArray = new ArrayList<>(this.membros);
        membrosArray.add(membro);
        this.membros = membrosArray;

    }
}
