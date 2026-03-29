package br.gov.es.invest.dto;

import java.util.List;

public record ObjetoFiltroDTO(
    Boolean audiencia,
    String nome,
    List<UnidadeOrcamentariaDTO> unidades,
    List<PlanoOrcamentarioDTO> planos,
    Integer exercicio,
    Integer gnd,
    EtapaDTO etapa,
    StatusDTO status,
    boolean podeVerUnidades,
    int tamPag,
    int pagAtual,
    List<OrdemItemDto> ordem
) {

}
