package br.gov.es.invest.dto;

import java.util.List;

public record ExecutarAcaoDTO(
    String acaoId,
    List<ApontamentoDTO> apontamentos,
    ParecerDTO parecer,
    ObjetoDto objeto
) {
    
}
