package br.gov.es.invest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RegistroDadoDetalhadoValoresPorAno {
    private int ano;
    private double contratado;
    private double planejado;
    
}
