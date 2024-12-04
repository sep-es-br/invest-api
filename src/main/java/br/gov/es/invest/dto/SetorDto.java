package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.SetorACResponseDto;
import br.gov.es.invest.model.Setor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
public class SetorDto {
    private String id;
    private String guid;
    private String nome;
    private String sigla;

    private OrgaoDto orgao;
    
    public SetorDto(SetorACResponseDto setorAC){
        this.guid = setorAC.guid();
        this.nome = setorAC.nome();
        this.sigla = setorAC.nomeCurto();

    }

    public SetorDto(Setor setor){
        this.id = setor.getId();
        this.guid = setor.getGuid();
        this.nome = setor.getNome();
        this.sigla = setor.getSigla();
        this.orgao = setor.getOrgao() == null ? null : new OrgaoDto(setor.getOrgao());
    }

}
