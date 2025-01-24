package br.gov.es.invest.dto;

import br.gov.es.invest.model.IndicadaPor;

public record IndicadaPorDto(
    String id,
    FonteOrcamentariaDTO fonteOrcamentaria,
    Double previsto,
    Double contratado
) {

    public IndicadaPorDto(IndicadaPor model) {
        this(
            model.getId(),
            new FonteOrcamentariaDTO(model.getFonteOrcamentaria()), 
            model.getPrevisto(), 
            model.getContratado()
        );
        
    }
} 