package br.gov.es.invest.dto;

import java.util.HashSet;
import java.util.Set;

import br.gov.es.invest.model.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDto {
        private String id;
        private String token;
        private String sub;
        private AvatarDTO imgPerfil;
        private String name;
        private String nomeCompleto;
        private String email;
        private String telefone;
        private String papel;
        private Set<FuncaoDTO> role;

        public UsuarioDto(Usuario usuario) {
                this.id = usuario.getId();
                this.sub = usuario.getSub();
                this.imgPerfil = usuario.getImgPerfil() == null ? null : new AvatarDTO(usuario.getImgPerfil());
                this.name = usuario.getName();
                this.nomeCompleto = usuario.getNomeCompleto();
                this.telefone = usuario.getTelefone();
                this.email = usuario.getEmail();
                this.papel = usuario.getPapel();
                this.role = usuario.getRole() == null ? new HashSet<>() : new HashSet<>(usuario.getRole().stream().map(funcao -> new FuncaoDTO(funcao)).toList());
        }
        
}
