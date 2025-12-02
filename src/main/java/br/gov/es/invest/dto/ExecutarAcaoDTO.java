package br.gov.es.invest.dto;

import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import java.util.List;

public record ExecutarAcaoDTO(
    AcaoDTO acao,
    List<ApontamentoDTO> apontamentos,
    ParecerDTO parecer,
    ObjetoCadastroFormDto objeto
) {
    
}
