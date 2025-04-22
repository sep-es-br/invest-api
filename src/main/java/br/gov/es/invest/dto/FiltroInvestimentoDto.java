package br.gov.es.invest.dto;

import java.util.List;


public record FiltroInvestimentoDto(
    String nome,
    Integer ano,
    List<PlanoOrcamentarioDTO> planos,
    List<UnidadeOrcamentariaDTO> unidades,
    FonteOrcamentariaDTO fonte,
    Integer gnd,
    boolean podeVerUnidades,
    Integer numPag,
    Integer qtPorPag,
    List<OrdemItemDto> ordem
    
) {
    
    
}
