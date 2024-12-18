package br.gov.es.invest.dto;

import java.util.List;

public record CadastroMembroFormDto(
    GrupoDTO grupo,
    OrgaoDto orgao,
    SetorDto setor,
    List<PapelDto> papeis
) {
    
}
