package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class PlanoOrcamentario extends Entidade implements Serializable{
    
    private Long codigo;
    private String nome;
    private String descricao;

    @Relationship(type = "ORIENTA", direction = Direction.OUTGOING)
    private ArrayList<Conta> contasOrientadas = new ArrayList<>();

    public PlanoOrcamentario(Long codigo, String nome, List<ExecucaoOrcamentaria> execucoes) {
        this.codigo = codigo;
        this.nome = nome;
    }

}
