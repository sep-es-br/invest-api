package br.gov.es.invest.dto;

import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.utils.domains.Fluxo;
import java.util.List;
import java.util.Optional;

public record FluxoDTO(
    String nome,
    String fluxoId,
    List<EtapaDTO> etapas
) {
    public FluxoDTO (Fluxo model, GrupoService grupoSrv) {
        this(
            model.nome(),
            model.fluxoId(),
            Optional.ofNullable(model.etapas()).map(_etapas -> _etapas.stream().map(etapa -> EtapaDTO.parse(etapa, grupoSrv)).toList()).orElse(null)
        );
    }

    public static FluxoDTO parse(Fluxo model, GrupoService grupoSrv) {
        return model == null ? null
        : new FluxoDTO(model, grupoSrv);
    }
}
