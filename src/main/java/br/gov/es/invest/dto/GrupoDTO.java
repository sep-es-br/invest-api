package br.gov.es.invest.dto;

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

    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.icone = grupo.getIcone();
        this.sigla = grupo.getSigla();
        this.nome = grupo.getNome();
        this.descricao = grupo.getDescricao();
    }

}
