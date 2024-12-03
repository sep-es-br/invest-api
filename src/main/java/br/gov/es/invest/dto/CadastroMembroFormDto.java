package br.gov.es.invest.dto;

public record CadastroMembroFormDto(
    GrupoDTO grupo,
    OrgaoDto orgao,
    SetorDto setor,
    PapelDto papel
) {
    
}
