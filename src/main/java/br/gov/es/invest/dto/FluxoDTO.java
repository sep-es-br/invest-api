package br.gov.es.invest.dto;

import br.gov.es.invest.utils.domains.Fluxo;
import java.util.List;
import java.util.Optional;

public record FluxoDTO(
    String nome,
    String fluxoId,
    List<EtapaDTO> etapas
) {
    public FluxoDTO (Fluxo model) {
        this(
            model.nome(),
            model.fluxoId(),
            Optional.ofNullable(model.etapas()).map(_etapas -> _etapas.stream().map(EtapaDTO::parse).toList()).orElse(null)
        );
    }

    public static FluxoDTO parse(Fluxo model) {
        return model == null ? null
        : new FluxoDTO(model);
    }
}
