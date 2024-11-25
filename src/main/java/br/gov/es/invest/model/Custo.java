package br.gov.es.invest.model;

import java.io.Serializable;

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
public class Custo extends Entidade implements Serializable {
     
    private double previsto;
    private double contratado;

    @Relationship(type = "EM", direction = Direction.OUTGOING)
    private Ano anoExercicio;

    @Relationship(type = "ESTIMADO", direction = Direction.OUTGOING)
    private Objeto objetoEstimado;

    @Relationship(type = "INDICADA", direction = Direction.INCOMING)
    private FonteOrcamentaria fonteOrcamentariaIndicadora;


    public Custo(Ano anoExercicio, double previsto, double contratado, Objeto objetoEstimado) {
        this.anoExercicio = anoExercicio;
        this.previsto = previsto;
        this.contratado = contratado;
        this.setObjetoEstimado(objetoEstimado);
    }

    public void setValores(double previsto, double contratado, FonteOrcamentaria fonte){
        this.previsto = previsto;
        this.contratado = contratado;
        this.fonteOrcamentariaIndicadora = fonte;
    }

}
