package br.gov.es.invest.dto.acessocidadaoapi;

public record SetorACResponseDto(
    String guid,
    String nome,
    String sigla,
    String nomeCurto,
    TipoUnidade tipoUnidade,
    UnidadePai unidadePai
) {}

record TipoUnidade(
    int id,
    String descricao
){}

record UnidadePai(
    String guid,
    String nome,
    String sigla,
    String nomeCurto
){}