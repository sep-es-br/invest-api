package br.gov.es.invest.dto;

import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.utils.components.FluxoConfig;
import java.util.List;

import br.gov.es.invest.utils.domains.Etapa;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public record EtapaDTO(
    Integer ordem,
    String nome,
    String etapaId,
    GrupoDTO grupoResponsavel,
    List<AcaoDTO> acoes
) {
    
    public static EtapaDTO parse(br.gov.es.invest.model.Etapa model, FluxoConfig fluxoConfig, GrupoService grupoSrv) {
        
        return Optional.ofNullable(model)
                .map(br.gov.es.invest.model.Etapa::getEtapaId)
                .map(Enum::name)
                .map(fluxoConfig.getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP)::etapa)
                .map(etapa -> EtapaDTO.parse(etapa, grupoSrv))
                .orElse(null);
        
    }
    
    public static EtapaDTO parse(Etapa model, GrupoService grupoSrv){
        if(model == null) return null;

        return new EtapaDTO(
            model.ordem(), 
            model.nome(), 
            model.etapaId(),
            GrupoDTO.parse(grupoSrv.findById(model.grupoResponsavel()).orElseThrow()), 
           Optional.ofNullable(model.acoes()).map(_acoes -> _acoes.stream().map(AcaoDTO::parse).toList()).orElse(null)
        );
    }
}
