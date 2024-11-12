package br.gov.es.invest.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
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

    private List<Custo> custosIndicados;

    public FonteOrcamentariaDTO ( FonteOrcamentaria fonte ) {
        this.id = fonte.getId();
        this.nome = fonte.getNome();
        this.descricao = fonte.getDescricao();
        this.custosIndicados = fonte.getCustosIndicados();
    }
}
