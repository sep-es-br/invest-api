package br.gov.es.invest.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Grupo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrupoDTO {
    private String id;
    private String icone;
    private String sigla;
    private String nome;
    private String descricao;

    
    private Set<UsuarioDto> membros; 

    private Set<PodeDto> permissoes;


    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.icone = grupo.getIcone();
        this.sigla = grupo.getSigla();
        this.nome = grupo.getNome();
        this.descricao = grupo.getDescricao();
        
        if(grupo.getMembros() != null)
            this.membros = grupo.getMembros().stream().map(usuario -> new UsuarioDto(usuario)).collect(Collectors.toSet());

        if(grupo.getPermissoes() != null)
            this.permissoes = grupo.getPermissoes().stream().map(permissao -> new PodeDto(permissao)).collect(Collectors.toSet());
    }

}
