package br.gov.es.invest.dto;

import java.util.List;

public record ExecutarAcaoDTO(
    AcaoDTO acao,
    List<ApontamentoDTO> apontamentos,
    ParecerDTO parecer,
    ObjetoDto objeto
) {
    
}
