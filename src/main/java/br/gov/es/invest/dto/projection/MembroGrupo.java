package br.gov.es.invest.dto.projection;

public record MembroGrupo(
    Long id,
    Integer nvl,
    String icone,
    String nomeCompleto,
    String papel,
    String setor,
    String orgao
) {}
