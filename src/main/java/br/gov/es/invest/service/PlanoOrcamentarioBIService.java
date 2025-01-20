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

import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;

@Service
public class PlanoOrcamentarioBIService extends PentahoBIService{
    
    @Value("${pentahoBI.spo.path}") // /public/dashboard/spo
    private String spoPath;

    @Value("${pentahoBI.spo.planoOrcamentario}")
    private String planosTarget;

    public List<PlanoOrcamentario> getPlanosPorUnidade(String codUnidade){
       
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("parampCodUo", codUnidade);


            String url = buildEndpointUri(spoPath, planosTarget, params);
            List<Map<String, JsonNode>> dados = extractDataFromResponse(doRequest(url));

            List<PlanoOrcamentario> planos = dados.stream().map(
                dado -> {
                    PlanoOrcamentario plano = new PlanoOrcamentario();
                    plano.setCodigo(dado.get("cod_po").asText());
                    plano.setNome(dado.get("nome_po").asText());
                    return plano;
                }
            ).toList();

            return planos;
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }



}
