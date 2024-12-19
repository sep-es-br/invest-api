package br.gov.es.invest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private boolean podeVerTodasUnidades;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private HashSet<Usuario> membros = new HashSet<>(); 

    @Relationship(type = "PODE")
    private Set<Pode> permissoes;

    public Grupo(GrupoDTO dto){
        this.setId(dto.getId());
        this.sigla = dto.getSigla();
        this.icone = dto.getIcone();
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
        this.podeVerTodasUnidades = dto.getPodeVerTodasUnidades();

        if(dto.getMembros() != null)
            this.membros.addAll(dto.getMembros().stream().map(membroDto -> new Usuario(membroDto)).collect(Collectors.toSet()));
        
        if(dto.getPermissoes() != null)
            this.permissoes = dto.getPermissoes().stream().map(permissao -> new Pode(permissao)).collect(Collectors.toSet());
    }
}
