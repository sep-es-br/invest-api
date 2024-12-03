package br.gov.es.invest.dto;

import java.util.List;

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

    
    private List<UsuarioDto> membros; 


    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.icone = grupo.getIcone();
        this.sigla = grupo.getSigla();
        this.nome = grupo.getNome();
        this.descricao = grupo.getDescricao();
        
        this.membros = grupo.getMembros().stream().map(usuario -> new UsuarioDto(usuario)).toList();

    }

}
