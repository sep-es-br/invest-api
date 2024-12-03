package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;

public record PapelDto(
    String id,
    String guid,
    String nome,
    String agenteSub,
    String agenteNome
) {
    
    public PapelDto(PapelACResponseDto papelAC){
        this(null, papelAC.Guid(), papelAC.Nome(), papelAC.AgentePublicoSub(), papelAC.AgentePublicoNome());
    }

}
