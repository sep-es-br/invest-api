package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Node
public class Investimento extends Conta{
    
    public Investimento(String nome) {
        super.setNome(nome);
    }

}
