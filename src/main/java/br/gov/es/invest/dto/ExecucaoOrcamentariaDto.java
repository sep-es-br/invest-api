package br.gov.es.invest.dto;

import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.VinculadaPor;

public record ExecucaoOrcamentariaDto(
    String id,
    Integer anoExercicio,
    Set<VinculadaPorDto> vinculadaPor
) {
    
    public static ExecucaoOrcamentariaDto parse(ExecucaoOrcamentaria model) {
        return model == null ? null :
            new ExecucaoOrcamentariaDto(
                model.getId(), 
                model.getAnoExercicio(), 
                model.getVinculadaPor() == null ? null : model.getVinculadaPor().stream().map(VinculadaPorDto::parse).collect(Collectors.toSet())
            );
    }

}
