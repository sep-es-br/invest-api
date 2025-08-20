package br.gov.es.invest.dto;

import br.gov.es.invest.utils.components.FluxoConfig;
import java.util.List;

import br.gov.es.invest.utils.domains.Etapa;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public record EtapaDTO(
    Integer ordem,
    String nome,
    String etapaId,
    String grupoResponsavel,
    List<AcaoDTO> acoes
) {
    
    public static EtapaDTO parse(br.gov.es.invest.model.Etapa model, FluxoConfig fluxoConfig) {
        
        return Optional.ofNullable(model)
                .map(br.gov.es.invest.model.Etapa::getEtapaId)
                .map(Enum::name)
                .map(fluxoConfig.getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP)::etapa)
                .map(EtapaDTO::parse)
                .orElse(null);
        
    }
    
    public static EtapaDTO parse(Etapa model){
        if(model == null) return null;

        return new EtapaDTO(
            model.ordem(), 
            model.nome(), 
            model.etapaId(),
            model.grupoResponsavel(), 
           Optional.ofNullable(model.acoes()).map(_acoes -> _acoes.stream().map(AcaoDTO::parse).toList()).orElse(null)
        );
    }
}
