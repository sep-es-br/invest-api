package br.gov.es.invest.dto;

import java.io.Serializable;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustoDTO implements Serializable {
    
    private String id;
    private String anoExercicio;
    private double previsto;
    private double contratado;

    private Objeto objetoEstimado;
    private FonteOrcamentaria fonteOrcamentariaIndicadora;

    public CustoDTO(Custo custo) {
        this.id = custo.getId();
        this.anoExercicio = custo.getAnoExercicio();
        this.previsto = custo.getPrevisto();
        this.contratado = custo.getContratado();

        this.objetoEstimado = custo.getObjetoEstimado();
        this.fonteOrcamentariaIndicadora = custo.getFonteOrcamentariaIndicadora();

    }

}
