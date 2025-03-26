package br.gov.es.invest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistroDadoConsolidado {
    private String unidadeOrcamentaria;
    private Double previsto;
    private Double contratado;
    private Double autorizado;
    private Double difAutorizadoContratado;
}
