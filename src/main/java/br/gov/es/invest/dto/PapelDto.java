package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.model.Papel;

public record PapelDto(
    Long id,
    String guid,
    String nome,
    SetorDto setor,
    Boolean prioritario,

    
    String agenteSub,
    String agenteNome

) {
    
    public PapelDto(PapelACResponseDto papelAC){
        this(
            null, 
            papelAC.Guid(), 
            papelAC.Nome(), 
            null, 
            papelAC.Prioritario(),
            papelAC.AgentePublicoSub(), 
            papelAC.AgentePublicoNome()
            );
    }

    public static PapelDto parse(Papel papel) {
        return papel == null ? null
        : new PapelDto(
            papel.getId(), 
            papel.getGuid(), 
            papel.getNome(), 
            papel.getSetor() == null ? null : new SetorDto(papel.getSetor()), 
            papel.getPrioritario(), 
            null, 
            null
        );
    }

}
