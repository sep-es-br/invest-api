package br.gov.es.invest.dto;

import java.io.Serializable;
import java.util.List;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.FonteOrcamentaria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FonteOrcamentariaDTO implements Serializable {

    private String id;
    private String nome;
    private String descricao;

    public FonteOrcamentariaDTO ( FonteOrcamentaria fonte ) {
        this.id = fonte.getId();
        this.nome = fonte.getNome();
        this.descricao = fonte.getDescricao();
    }
}
