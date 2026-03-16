package br.gov.es.invest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record  RegistroDadoConsolidado(
    String unidadeOrcamentaria,
    Double planejado,
    Double contratado,
    Double autorizado,
    Double orcado,
    Double empenhadoAnt,
    Double empenhado,
    Double liquidado,
    Double pago,
    Double difAutorizadoContratado,
    Double difAutorizadoEmpenhadoAnt
) {
}
