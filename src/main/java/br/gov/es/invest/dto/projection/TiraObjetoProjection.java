package br.gov.es.invest.dto.projection;

public record TiraObjetoProjection(
    Long id,
    String nome,
    String codPo,
    String unidadeOrcamentaria,
    String status,
    String tipo,
    Double totalPrevisto,
    Double totalContratado,
    Double totalOrcado,
    Double totalAutorizado,
    Double totalEmpenhado,
    Double totalDisponivel
) {
    
}
