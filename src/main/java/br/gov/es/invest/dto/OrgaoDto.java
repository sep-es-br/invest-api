package br.gov.es.invest.dto;

import java.util.Optional;

import br.gov.es.invest.model.Orgao;

public record OrgaoDto(
        Long id,
        String guid,
        String sigla,
        String nome
    ) {
        
    public OrgaoDto(Orgao orgao){
        this(orgao.getId(), orgao.getGuid(), orgao.getSigla(), orgao.getNome());
    }

    public static OrgaoDto parse(Orgao orgao) {
        return Optional.ofNullable(orgao).map(OrgaoDto::new).orElse(null);
    }

}
