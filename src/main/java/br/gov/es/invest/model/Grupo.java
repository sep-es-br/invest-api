package br.gov.es.invest.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.GrupoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Grupo extends Entidade {
    private String sigla;
    private String icone;
    private String nome;
    private String descricao;

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private Set<Usuario> membros = new HashSet<>();

    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private Set<Papel> papeisMembro = new HashSet<>();
    
    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private Set<Setor> setoresMembro = new HashSet<>();
    
    @Relationship(type = "MEMBRO_DE", direction = Direction.INCOMING)
    private Set<Orgao> orgaosMembro = new HashSet<>();

    @Relationship(type = "PODE")
    private Set<Pode> permissoes;

    public Grupo(GrupoDTO dto){

        this.setId(dto.id());
        this.sigla = dto.sigla();
        this.icone = dto.icone();
        this.nome = dto.nome();
        this.descricao = dto.descricao();

        this.membros = Optional.ofNullable(dto.membros())
        .orElse(Collections.emptySet()).stream().map(Usuario::parse).collect(Collectors.toSet());

        
        if(dto.papeisMembro() != null)
            this.papeisMembro.addAll(dto.papeisMembro().stream().map(
                Papel::parse
            ).collect(Collectors.toSet()));

        if(dto.setoresMembros() != null)
            this.setoresMembro.addAll(dto.setoresMembros().stream().map(
                setor -> new Setor(setor)
            ).collect(Collectors.toSet()));

        if(dto.orgaoMembro() != null)
            this.orgaosMembro.addAll(dto.orgaoMembro().stream().map(
                orgao -> new Orgao(orgao)
            ).collect(Collectors.toSet()));
        
        this.permissoes = Optional.ofNullable(dto.permissoes())
        .orElse(Collections.emptySet()).stream().map(permissao -> new Pode(permissao)).collect(Collectors.toSet());

    }

    public static Grupo parse(GrupoDTO dto) {
        
        return Optional.ofNullable(dto).map(Grupo::new).orElse(null);
        
    }
}
