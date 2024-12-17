package br.gov.es.invest.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Objeto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustoDTO implements Serializable {
    
    private String id;
    private Integer anoExercicio;

    private Set<IndicadaPorDto> indicadaPor;

    public CustoDTO(Custo custo) {
        this.id = custo.getId();
        this.anoExercicio = custo.getAnoExercicio();
        

        this.indicadaPor = custo.getIndicadaPor().stream().map(indicadaPor -> new IndicadaPorDto(indicadaPor)).collect(Collectors.toSet());

    }

}
