package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import br.gov.es.invest.model.Orgao;

public record OrgaoDto(
        String id,
        String guid,
        String sigla,
        String nome,
        List<SetorDto> setores
    ) {
        
    public OrgaoDto(Orgao orgao){
        this(orgao.getId(), orgao.getGuid(), orgao.getSigla(), orgao.getNome(), orgao.getSetores().stream().map(setor -> new SetorDto(setor)).toList());
    }

}
