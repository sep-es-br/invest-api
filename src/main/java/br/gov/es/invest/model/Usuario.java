package br.gov.es.invest.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.UsuarioDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Usuario extends Entidade {
    private String sub;
    private String name;
    private String email;
    private String cpf;

    @Relationship(type = "POSSUI")
    private Avatar imgPerfil;

    @Relationship(type = "ATUA_COMO")
    private Set<Funcao> role;

    public Usuario(UsuarioDto dto){

        this.setId(dto.getId());
        this.sub = dto.getSub();
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.cpf = dto.getCpf();

        this.imgPerfil = dto.getImgPerfil() == null ? null : new Avatar(dto.getImgPerfil());
        this.role = (dto.getRole() == null ) ? null : new HashSet<>(dto.getRole().stream().map(funcao -> new Funcao(funcao)).toList());
    }

    public void setRole(Set<String> roles) {
        this.role = new HashSet<>(roles.stream().map(role -> new Funcao(role)).toList());
    }
}
