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

import br.gov.es.invest.biClient.BiClient;
import br.gov.es.invest.model.PlanoOrcamentario;

@Service
public class PlanoOrcamentarioBIService extends PentahoBIService{
    
    @Value("${pentahoBI.spo.path}") // /public/dashboard/spo
    private String spoPath;

    @Value("${pentahoBI.spo.planoOrcamentario}")
    private String planosTarget;

    @Value("${pentahoBI.spo.resouce.planoOrcamentario}")
    private String resource;

    @Autowired
    private PlanoOrcamentarioService planoOrcamentarioService;

    @Autowired
    private BiClient biClient;

    public List<PlanoOrcamentario> getPlanosPorUnidade(String codUnidade){
       

        HashMap<String, String> params = new HashMap<>();
        params.put("parampCodUo", codUnidade);
        params.put("parampCodPo", "todos");

        List<Map<String, JsonNode>> dados = biClient.doQuery(resource, params);


        List<PlanoOrcamentario> planos = dados.stream().map(
            dado -> PlanoOrcamentario.builder()
                    .codigo(dado.get("cod_po").asText())
                    .nome(dado.get("nome_po").asText())
                    .build()
        ).collect(Collectors.toList());
        return planos;
        
    }

    public PlanoOrcamentario getPlanoPorCod(String codPo){
       
        HashMap<String, String> params = new HashMap<>();
        params.put("parampCodUo", "todas");
        params.put("parampCodPo", codPo);

        List<Map<String, JsonNode>> dados = biClient.doQuery(resource, params);

        return dados.stream().map(
            dado -> PlanoOrcamentario.builder()
                    .id(planoOrcamentarioService.getIdByCod(dado.get("cod_po").asText()))
                    .codigo(dado.get("cod_po").asText())
                    .nome(dado.get("nome_po").asText())
                    .build()
        ).findFirst().orElse(null);


    }



}
