package br.gov.es.invest.dto;

import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.utils.components.FluxoConfig;

public record EmEtapaDTO(
    String id,
    EtapaDTO etapa,
    String atividade,
    boolean devolvido
) {
    public static EmEtapaDTO parse(EmEtapa model, FluxoConfig fluxoConfig, GrupoService grupoSrv) {
        return model == null ? null
        : new EmEtapaDTO(
            model.getId(), 
            EtapaDTO.parse(model.getEtapa(), fluxoConfig, grupoSrv), 
            model.getAtividade(),
            model.isDevolvido()
        );
    }
}
