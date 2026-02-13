package br.gov.es.invest.dto.projection;

public record TiraInvestimentoProjection(
    Long id,
    String nome,
    String codPO,
    String unidadeOrcamentaria,
    Double totalPlanejado,
    Double totalContratado,
    Double totalOrcado,
    Double totalAutorizado,
    Double totalEmpenhado,
    Double totalDisponivel
) {
    
}
