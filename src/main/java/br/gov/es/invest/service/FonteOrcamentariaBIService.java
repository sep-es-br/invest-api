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

import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.PlanoOrcamentario;

@Service
public class FonteOrcamentariaBIService extends PentahoBIService {
    
    @Value("${pentahoBI.spo.path}") 
    private String spoPath;

    @Value("${pentahoBI.spo.fontesOrcamentarias}")
    private String targetFonteOrcamentaria;

    public List<FonteOrcamentaria> getFontes(){
       
        try {
            String url = buildEndpointUri(spoPath, targetFonteOrcamentaria, null);
            List<Map<String, JsonNode>> dados = extractDataFromResponse(doRequest(url));

            List<FonteOrcamentaria> fontes = dados.stream().map(
                dado -> {
                    FonteOrcamentaria fonte = new FonteOrcamentaria();
                    fonte.setCodigo(dado.get("cod_fonte").asText());
                    fonte.setNome(dado.get("nome_fonte").asText());
                    return fonte;
                }
            ).toList();

            return fontes;
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }

}
