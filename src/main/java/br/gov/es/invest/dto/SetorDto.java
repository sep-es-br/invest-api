package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.SetorACResponseDto;
import br.gov.es.invest.model.Setor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetorDto {
    private String id;
    private String guid;
    private String nome;
    private String sigla;
    
    public SetorDto(SetorACResponseDto setorAC){
        this(null, setorAC.guid(), setorAC.nome(), setorAC.nomeCurto());
    }

    public SetorDto(Setor setor){
        this(setor.getId(), setor.getGuid(), setor.getNome(), setor.getSigla());
    }

}
