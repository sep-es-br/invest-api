package br.gov.es.invest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadoConsolidadoDTO(
    String unidadeOrcamentaria,
    Double planejado,
    Double contratado,
    Double autorizado,
    Double difAutorizadoContratado
) {
    
}
