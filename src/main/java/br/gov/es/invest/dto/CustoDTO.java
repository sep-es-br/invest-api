package br.gov.es.invest.dto;

import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Custo;

public record CustoDTO(
    Long id,
    Integer anoExercicio,
    Set<IndicadaPorDto> indicadaPor
){
    public static CustoDTO parse(Custo custo){
        return custo == null ? null 
        : new CustoDTO(
            custo.getId(), 
            custo.getAnoExercicio(), 
            custo.getIndicadaPor().stream()
                .map(indicadaPor -> new IndicadaPorDto(indicadaPor))
                .sorted((ip1, ip2) -> ip1.fonteOrcamentaria().getCodigo().compareTo(ip2.fonteOrcamentaria().getCodigo()))
                .collect(Collectors.toSet())
        );
    }
}
