package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
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
public class FonteOrcamentaria extends Entidade implements Serializable {

    private String nome;
    private String descricao;

    @Relationship(type = "INDICADA", direction = Direction.OUTGOING)
    private ArrayList<Custo> custosIndicados = new ArrayList<>();

    @Relationship(type = "VINCULA", direction = Direction.OUTGOING)
    private ArrayList<ExecucaoOrcamentaria> execucoesOrcamentariaVinculadas = new ArrayList<>();

    public FonteOrcamentaria(String nome) {
        this.nome = nome;
    }

    public FonteOrcamentaria(String codigo, String nome, List<Custo> custos, List<ExecucaoOrcamentaria> execucoes) {
        this.setId(codigo);
        this.nome = nome;
        custos.forEach(this.custosIndicados::add);
        execucoes.forEach(this.execucoesOrcamentariaVinculadas::add);
    }

    public static FonteOrcamentaria criar(String codigo, String nome){
        FonteOrcamentaria novo = new FonteOrcamentaria();
        novo.setId(codigo);
        novo.nome = nome;
        DataMock.noFonteOrcamentarias.add(novo);
        return novo;
    }
    

}