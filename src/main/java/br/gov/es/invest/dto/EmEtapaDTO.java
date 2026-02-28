package br.gov.es.invest.dto;

import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.utils.DateTimeUtils;
import java.util.Optional;

public record EmEtapaDTO(
    Long id,
    EtapaDTO etapa,
    String atividade,
    boolean devolvido,
    String timestamp
) {
    public static EmEtapaDTO parse(EmEtapa model) {
        return model == null ? null
        : new EmEtapaDTO(
            model.getId(), 
            EtapaDTO.parse(model.getEtapa()), 
            model.getAtividade(),
            model.isDevolvido(),
            Optional.ofNullable(model.getTimestamp()).map(DateTimeUtils::formatZonedDateTime).orElse(null)
        );
    }
}
