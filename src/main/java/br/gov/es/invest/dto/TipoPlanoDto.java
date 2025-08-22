package br.gov.es.invest.dto;

import br.gov.es.invest.model.TipoPlano;

public record TipoPlanoDto(
    Long id,
    String nome,
    String sigla
) {
    public TipoPlanoDto(TipoPlano model){
        this(
            model.getId(), 
            model.getNome(), 
            model.getSigla()
        );
    }
}
