package br.gov.es.invest.model;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.usuario.SalvarUsuarioForm;
import java.beans.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Node
public class Agente extends Entidade {
    
    private String sub;
    private String name;
    private String nomeCompleto;
    private String telefone;
    private String email;
    private LocalDateTime deletadoEm;

    @Relationship(type = "POSSUI")
    private Avatar imgPerfil;

    @Relationship(type = "ATUA_COMO")
    private Set<Funcao> role;

    @Relationship("POSSUI")
    private List<Papel> papeis;

    public Agente(UsuarioDto dto){

        this.setId(dto.id());
        this.sub = dto.sub();
        this.name = dto.name();
        this.nomeCompleto = dto.nomeCompleto();
        this.telefone = dto.telefone();
        
        this.email = dto.email();

        this.imgPerfil = Avatar.parse(dto.imgPerfil());
        this.role = Optional.ofNullable(dto.role())
            .map(roles -> roles.stream().map(Funcao::new).collect(Collectors.toSet())).orElse(null);

    }

    public Agente(ACUserInfoDto acUser) {
        this.nomeCompleto = acUser.apelido();
        this.sub = acUser.subNovo();
        this.email = acUser.emailCorporativo() == null ? acUser.email() : acUser.emailCorporativo();
    }

    public void setRole(Set<String> roles) {
        this.role = new HashSet<>(roles.stream().map(role -> new Funcao(role)).toList());
    }
    
    @Transient
    public Papel getPapelAtivo() {
        if(this.papeis.size() == 1) {
            return this.papeis.get(0);
        } else {
            return this.papeis.stream().filter(Papel::getPrioritario).findFirst().orElse(this.papeis.get(0));
        }
    }
    
    public void set(SalvarUsuarioForm form) {
        this.name = form.nome();
        this.nomeCompleto = form.nomeCompleto();
        this.email = form.email();
        this.telefone = form.telefone();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Agente)) return false;

        Agente other = (Agente) obj;

        return this.sub.equals(other.getSub());
        
    }

    public static Agente parse(UsuarioDto dto) {
        return dto == null ? null
        : new Agente(dto);
    }

}
