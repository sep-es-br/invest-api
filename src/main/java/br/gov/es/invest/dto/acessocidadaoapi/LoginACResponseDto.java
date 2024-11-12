package br.gov.es.invest.dto.acessocidadaoapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginACResponseDto(
        @JsonProperty(value = "access_token")
        String accessToken,
        @JsonProperty(value = "expires_in")
        int expiresIn,
        @JsonProperty(value = "token_type")
        String tokenType) {
}
