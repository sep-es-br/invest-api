package br.gov.es.invest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DadosConsolidadosValores {
    private String idFonte;
    private String nomeFonte;
    private Double valorPrevisto;
    private Double valorContratado;
}
