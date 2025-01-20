package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public List<UnidadeOrcamentaria> getTodasUnidades(){
       
        try {
            String url = buildEndpointUri(spoPath, unidadesTarget, null);
            List<Map<String, JsonNode>> dados = extractDataFromResponse(doRequest(url));

            List<UnidadeOrcamentaria> unidades = dados.stream().map(
                dado -> {
                    UnidadeOrcamentaria unidade = new UnidadeOrcamentaria();
                    unidade.setCodigo(dado.get("cod_uo").asText());
                    unidade.setSigla(dado.get("sigla_uo").asText());
                    unidade.setNome(dado.get("nome_uo").asText());
                    return unidade;
                }
            ).toList();

            return unidades;
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }
}
