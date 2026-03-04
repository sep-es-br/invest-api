package br.gov.es.invest.dto;

public record CardsTotaisDto(
    Double planejado,
    Double contratado,
    Double orcado,
    Double autorizado,
    Double empenhado,
    Double liquidado,
    Double dispSemReserva,
    Double pago
) {
    
}
