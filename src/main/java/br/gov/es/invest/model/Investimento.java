package br.gov.es.invest.model;

import org.bouncycastle.util.test.FixedSecureRandom.Data;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Node
public class Investimento extends Conta{
    
    public Investimento(String nome) {
        super.setNome(nome);
    }

    public static Investimento criar(String nome){
        Investimento novo = new Investimento(nome);
        DataMock.noInvestimentos.add(novo);
        return novo;
    }

}
