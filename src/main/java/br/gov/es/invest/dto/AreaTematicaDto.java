package br.gov.es.invest.dto;

import br.gov.es.invest.model.AreaTematica;

public record AreaTematicaDto(
    String id,
    String nome
) {

    public AreaTematicaDto(AreaTematica model) {
        this(
            model.getId(),
            model.getNome()
        );
    }
    
}
