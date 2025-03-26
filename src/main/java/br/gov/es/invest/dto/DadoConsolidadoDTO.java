package br.gov.es.invest.dto;

import lombok.Builder;

@Builder
public record DadoConsolidadoDTO(
    String unidadeOrcamentaria,
    Double previsto,
    Double contratado,
    Double autorizado,
    Double difAutorizadoContratado
) {
    
}
