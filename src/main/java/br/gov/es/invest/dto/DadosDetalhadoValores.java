package br.gov.es.invest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DadosDetalhadoValores {
    private Long idFonte;
    private String nomeFonte;
    private Double valorPlanejado;
    private Double valorContratado;
}
