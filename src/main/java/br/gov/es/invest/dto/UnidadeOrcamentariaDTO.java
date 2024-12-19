package br.gov.es.invest.dto;

import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import br.gov.es.invest.model.UnidadeOrcamentaria;

public record UnidadeOrcamentariaDTO(
        String id,
        String guid,
        String codigo,
        String nome,
        String sigla
    )  {
    
    public UnidadeOrcamentariaDTO(UnidadesACResponseDto unidadeAC){
        this(null, unidadeAC.guid(), "", unidadeAC.nomeFantasia(), unidadeAC.sigla());
    }

    public UnidadeOrcamentariaDTO(UnidadeOrcamentaria unidade) {
        this(unidade.getId(), unidade.getGuid(), unidade.getCodigo(), unidade.getNome(), unidade.getSigla());
    }

}
