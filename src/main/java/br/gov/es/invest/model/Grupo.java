package br.gov.es.invest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.GrupoDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Grupo extends Entidade {
    private String sigla;
    private String icone;
    private String nome;
    private String descricao;
    private boolean podeVerTodasUnidades;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private Set<Usuario> membros = new HashSet<>(); 

    @Relationship(type = "PODE")
    private Set<Pode> permissoes;

    public Grupo(GrupoDTO dto){

        this.setId(dto.id());
        this.sigla = dto.sigla();
        this.icone = dto.icone();
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.podeVerTodasUnidades = dto.podeVerTodasUnidades();

        this.membros = Optional.ofNullable(dto.membros())
        .orElse(Collections.emptySet()).stream().map(Usuario::parse).collect(Collectors.toSet());

        this.permissoes = Optional.ofNullable(dto.permissoes())
        .orElse(Collections.emptySet()).stream().map(permissao -> new Pode(permissao)).collect(Collectors.toSet());

    }

    public static Grupo parse(GrupoDTO dto) {
        
        return Optional.ofNullable(dto).map(Grupo::new).orElse(null);
        
    }
}
