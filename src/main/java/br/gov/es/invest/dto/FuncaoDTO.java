package br.gov.es.invest.dto;

import br.gov.es.invest.model.Funcao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FuncaoDTO {
    private String id;
    private String nome;

    public FuncaoDTO(Funcao funcao) {
        this.id = funcao.getId();
        this.nome = funcao.getNome();
    }
}
