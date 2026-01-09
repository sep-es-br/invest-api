package br.gov.es.invest.dto;

import lombok.Builder;

@Builder
public record  RegistroDadoConsolidado(
    String unidadeOrcamentaria,
    Double previsto,
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
