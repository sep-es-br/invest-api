package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Ano extends Entidade {
    
    private String ano;

    public Ano(String ano) {
        this.ano = ano;
    }

}
