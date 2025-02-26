package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Papel extends Entidade{
    
    private String nome;
    private String guid;
    private boolean prioritario;

    private Setor setor;


}
