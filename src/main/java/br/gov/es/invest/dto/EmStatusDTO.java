package br.gov.es.invest.dto;

import java.time.ZonedDateTime;

import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.utils.DateTimeUtils;

public record EmStatusDTO(
    String id,
    StatusDTO status,
    String timestamp    
) {
    public static EmStatusDTO parse (EmStatus model) {
        return model == null ? null
        : new EmStatusDTO(
            model.getId(),
            StatusDTO.parse(model.getStatus()), 
            DateTimeUtils.formatZonedDateTime(model.getTimestamp()) 
        );
    }
}
