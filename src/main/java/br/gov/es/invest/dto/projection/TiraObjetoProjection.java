package br.gov.es.invest.dto.projection;

public record TiraObjetoProjection(
    Long invId,
    Long id,
    String nome,
    String codPo,
    String unidadeOrcamentaria,
    String status,
    String tipo,
    Double totalPlanejado,
    Double totalContratado,
    Double totalOrcado,
    Double totalAutorizado,
    Double totalEmpenhado,
    Double totalDisponivel
) {
    
}
