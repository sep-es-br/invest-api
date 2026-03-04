package br.gov.es.invest.dto;

import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.utils.DateTimeUtils;
import java.util.Optional;

public record EmEtapaDTO(
    Long id,
    EtapaDTO etapa,
    String atividade,
    boolean devolvido,
    String timestamp,
    String avaliadoEm,
    String avaliadoPor
) {
    
}
