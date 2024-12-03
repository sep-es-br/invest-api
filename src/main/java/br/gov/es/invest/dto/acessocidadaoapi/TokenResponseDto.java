package br.gov.es.invest.dto.acessocidadaoapi;

public record TokenResponseDto(
    String access_token,
    int expires_in,
    String token_type
) {
}
