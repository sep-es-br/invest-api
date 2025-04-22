package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.model.UnidadeOrcamentaria;

@Service
public class UnidadeOrcamentariaBIService extends PentahoBIService {
    @Value("${pentahoBI.spo.path}") // /public/dashboard/spo
    private String spoPath;

    @Value("${pentahoBI.spo.unidadesOrcamentarias}")
    private String unidadesTarget;

    @Autowired
    private UnidadeOrcamentariaService unidadeOrcamentariaService;

    public List<UnidadeOrcamentaria> getTodasUnidades(){
       
        try {
            String url = buildEndpointUri(spoPath, unidadesTarget, null);
            List<Map<String, JsonNode>> dados = extractDataFromResponse(doRequest(url));

            List<UnidadeOrcamentaria> unidades = dados.stream()
            .filter( dado -> !dado.get("cod_uo").asText().startsWith("0") &&  !dado.get("cod_uo").asText().startsWith("8"))
            .map(
                dado -> UnidadeOrcamentaria.builder()
                        .id(unidadeOrcamentariaService.getIdByCod(dado.get("cod_uo").asText()))
                        .codigo(dado.get("cod_uo").asText())
                        .sigla(dado.get("sigla_uo").asText())
                        .nome(dado.get("nome_uo").asText())
                        .build()
            ).collect(Collectors.toList());

            return unidades;
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }
}
