package br.gov.es.invest.dto;

import br.gov.es.invest.model.IndicadaPor;

public record IndicadaPorDto(
    Long id,
    FonteOrcamentariaDTO fonteOrcamentaria,
    Double previsto,
    Double contratado,
    Integer gnd
) {

    public IndicadaPorDto(IndicadaPor model) {
        this(
            model.getId(),
            new FonteOrcamentariaDTO(model.getFonteOrcamentaria()), 
            model.getPrevisto(), 
            model.getContratado(),
            model.getGnd()
        );
        
    }
} 