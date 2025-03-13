package br.gov.es.invest.dto;

import java.util.List;

public record DadosConsolidadosDTO(
    String idUnidade,
    String unidadeResponsavel,
    String idPO,
    String codPO,
    String nomePO,
    Boolean projEstrategico,
    String contrato,
    Integer anoExercicio,
    List<DadosValores> dadosPrevisto,
    List<DadosValores> dadosContratado
) {
    
}

record DadosValores(
    String idFonte,
    String nomeFonte,
    String valor
){
    
}