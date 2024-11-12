package br.gov.es.invest.model;

import java.io.Serializable;
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
public class Custo extends Entidade implements Serializable {
     
    private String anoExercicio;
    private double previsto;
    private double contratado;

    @Relationship(type = "ESTIMADO", direction = Direction.OUTGOING)
    private Objeto objetoEstimado;

    @Relationship(type = "INDICADA", direction = Direction.INCOMING)
    private FonteOrcamentaria fonteOrcamentariaIndicadora;

    @Relationship(type = "INFORMA", direction = Direction.INCOMING)
    private UnidadeOrcamentaria unidadeOrcamentariaInformadora;

    public Custo(String anoExercicio, double previsto, double contratado, Objeto objetoEstimado) {
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

    public static Custo criar(String ano, UnidadeOrcamentaria unidade, Objeto objeto){
        Custo novo = new Custo();
        novo.anoExercicio = ano;
        novo.unidadeOrcamentariaInformadora = unidade;
        novo.objetoEstimado = objeto;
        DataMock.noCustos.add(novo);
        return novo;
    }

    public static Custo findOrCreate(String ano, UnidadeOrcamentaria unidadeOrcamentaria, Objeto objeto){
        List<Custo> result = DataMock.noCustos.stream()
            .filter(custo ->  {
                return custo.anoExercicio.equals(ano)
                    && custo.unidadeOrcamentariaInformadora.equals(unidadeOrcamentaria)
                    && custo.objetoEstimado.equals(objeto);
            }).toList();

        if(result.isEmpty()) {
            return Custo.criar(ano, unidadeOrcamentaria, objeto);
        } else {
            return result.get(0);
        }
    }

    public static Custo findOrCreate(String ano, Custo custoBase){
        return findOrCreate(ano, custoBase.unidadeOrcamentariaInformadora, custoBase.objetoEstimado);
    }

}
