package br.gov.es.invest.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Modulo;

public record ModuloDto(
    String id,
    String nome,
    String pathId,
    Set<ModuloDto> filhos
) {
    
    public ModuloDto(Modulo model) {
        this(
            model.getId(),
            model.getNome(), 
            model.getPathId(), 
            model.getFilhos() == null ? null : model.getFilhos().stream().map( filhoModel -> new ModuloDto(filhoModel) ).collect(Collectors.toSet())
        );
    }

}