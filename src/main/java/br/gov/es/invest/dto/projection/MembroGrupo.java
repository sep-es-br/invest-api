package br.gov.es.invest.dto.projection;

public record MembroGrupo(
    String id,
    Integer nvl,
    String icone,
    String nomeCompleto,
    String papel,
    String setor,
    String orgao
) {}
