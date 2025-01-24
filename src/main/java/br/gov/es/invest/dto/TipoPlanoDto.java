package br.gov.es.invest.dto;

import br.gov.es.invest.model.TipoPlano;

public record TipoPlanoDto(
    String id,
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
