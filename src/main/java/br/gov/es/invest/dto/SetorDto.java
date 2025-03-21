package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.SetorACResponseDto;
import br.gov.es.invest.model.Setor;
 
public record SetorDto(
    String id,
    String guid,
    String nome,
    String sigla,
    OrgaoDto orgao
) {
    
    public SetorDto(SetorACResponseDto setorAC, OrgaoDto orgao){
        this(
            null, 
            setorAC.guid(), 
            setorAC.nome(), 
            setorAC.nomeCurto(), 
            orgao
        );

    }

    public SetorDto(SetorACResponseDto setorAC){
        this(
            setorAC, 
            null
        );

    }

    public SetorDto(Setor setor){
        this(
            setor.getId(),
            setor.getGuid(),
            setor.getNome(),
            setor.getSigla(),
            OrgaoDto.parse(setor.getOrgao())
        );
    }

}
