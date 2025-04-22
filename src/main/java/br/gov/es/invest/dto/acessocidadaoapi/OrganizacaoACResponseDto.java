package br.gov.es.invest.dto.acessocidadaoapi;

public record OrganizacaoACResponseDto(
    String guid,
    String razaoSocial,
    String nomeFantasia,
    String sigla,
    String guidOrganizacaoPai
) {
    
}
