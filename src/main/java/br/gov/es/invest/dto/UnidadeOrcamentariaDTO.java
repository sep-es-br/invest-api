package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;

public record UnidadeOrcamentariaDTO(
        String id,
        String guid,
        String nome,
        String sigla
    )  {
    
    public UnidadeOrcamentariaDTO(UnidadesACResponseDto unidadeAC){
        this(null, unidadeAC.guid(), unidadeAC.nomeFantasia(), unidadeAC.sigla());
    }

}
