package br.gov.es.invest.dto;

import br.gov.es.invest.model.Orgao;

public record OrgaoDto(
        String id,
        String guid,
        String sigla,
        String nome
    ) {
        
    public OrgaoDto(Orgao orgao){
        this(orgao.getId(), orgao.getGuid(), orgao.getSigla(), orgao.getNome());
    }

}
