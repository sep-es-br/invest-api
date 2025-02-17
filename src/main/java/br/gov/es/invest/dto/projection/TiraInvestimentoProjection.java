package br.gov.es.invest.dto.projection;

public record TiraInvestimentoProjection(
    String id,
    String nome,
    String codPO,
    String unidadeOrcamentaria,
    Double totalPrevisto,
    Double totalContratado,
    Double totalOrcado,
    Double totalAutorizado,
    Double totalEmpenhado,
    Double totalDisponivel
) {
    
}
