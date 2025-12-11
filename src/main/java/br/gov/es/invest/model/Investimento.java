package br.gov.es.invest.model;

import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@SuperBuilder
public class Investimento extends Conta{
    
    public Investimento() {
        super(TIPO_CONTA.INVESTIMENTO);
    }
    
    public Investimento(String nome) {
        super(TIPO_CONTA.INVESTIMENTO);
        super.setNome(nome);
    }

}
