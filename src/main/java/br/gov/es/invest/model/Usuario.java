package br.gov.es.invest.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.UsuarioDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Usuario extends Entidade {
    private String ACToken;
    private String sub;
    private String name;
    private String nomeCompleto;
    private String telefone;
    private String email;
    private String papel;

    @Relationship(type = "POSSUI")
    private Avatar imgPerfil;

    @Relationship(type = "ATUA_COMO")
    private Set<Funcao> role;

    @Relationship(type ="MEMBRO_DE")
    private Setor setor;

    public Usuario(UsuarioDto dto){

        this.setId(dto.getId());
        this.sub = dto.getSub();
        this.name = dto.getName();
        this.nomeCompleto = dto.getNomeCompleto();
        this.telefone = dto.getTelefone();
        
        this.email = dto.getEmail();
        this.papel = dto.getPapel();

        this.imgPerfil = dto.getImgPerfil() == null ? null : new Avatar(dto.getImgPerfil());
        this.role = (dto.getRole() == null ) ? null : new HashSet<>(dto.getRole().stream().map(funcao -> new Funcao(funcao)).toList());

        this.setor = dto.getSetor() == null ? null : new Setor(dto.getSetor());
    }

    public Usuario(ACUserInfoDto acUser) {
        this.nomeCompleto = acUser.apelido();
        this.sub = acUser.subNovo();
        this.email = acUser.emailCorporativo() == null ? acUser.email() : acUser.emailCorporativo();
    }

    public void setRole(Set<String> roles) {
        this.role = new HashSet<>(roles.stream().map(role -> new Funcao(role)).toList());
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(!(obj instanceof Usuario)) return false;

        Usuario other = (Usuario) obj;

        return this.sub.equals(other.getSub());
        
    }

}
