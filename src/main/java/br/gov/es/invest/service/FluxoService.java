package br.gov.es.invest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.es.invest.utils.components.FluxoConfig;
import br.gov.es.invest.utils.domains.Fluxo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FluxoService {
        
    private final FluxoConfig fluxoConfig;
    
    public List<Fluxo> findAll() {
        return fluxoConfig.getFluxos();
    }

    public Fluxo findWithEtapa(String etapaId) {
        
        List<Fluxo> result = fluxoConfig.getFluxos().stream()
                .filter(fluxo -> fluxo.etapa(etapaId) != null)
                .toList();

        return result.isEmpty() ? null : result.get(0);

    }

    public Fluxo findByFluxoId(String fluxoId) {
        return fluxoConfig.getFluxo(fluxoId);
    }




}
