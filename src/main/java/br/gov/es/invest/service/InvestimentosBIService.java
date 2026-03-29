package br.gov.es.invest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class InvestimentosBIService extends PentahoBIService {
    
    @Value("${pentahoBI.spo.path}") // /public/dashboard/spo
    private String spoPath;

    @Value("${pentahoBI.spo.cardsTotais}")
    private String targetCardsTotais;

    @Value("${pentahoBI.spo.valorMes}")
    private String targetValorMes;

    @Value("${pentahoBI.spo.valorAno}")
    private String targetValorAno;

    public List<Map<String, JsonNode>> getCardsTotais(
        String codFonte, Integer exercicio, String codUnidade, String codPlano, Integer gnd
    ){

        codUnidade = codUnidade == null ? "todas" : codUnidade;
        codPlano = codPlano == null ? "todos" : codPlano;
        codFonte = codFonte == null ? "todas" : codFonte;
        String codGnd = gnd == null ? "todas" : String.valueOf(gnd);

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("parampCodUo", codUnidade);
        paramsMap.put("parampCodPO", codPlano);
        paramsMap.put("parampCodFonte", codFonte);
        paramsMap.put("parampAno", String.valueOf(exercicio) );
        paramsMap.put("parampCodGnd", codGnd);

        String url = buildEndpointUri(spoPath, targetCardsTotais, paramsMap);

        try {
            return extractDataFromResponse(doRequest(url));
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public List<Map<String, JsonNode>> getDadosPorMes(
        Integer anoInicial, Integer anoFinal
    ){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("parampAnoInicial", anoInicial.toString());
        paramsMap.put("parampAnoFinal", anoFinal.toString());

        String url = buildEndpointUri(spoPath, targetValorMes, paramsMap);

        try {
            return extractDataFromResponse(doRequest(url));
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }

    }

    public List<Map<String, JsonNode>> getDadosPorAno(
        Integer anoInicial, Integer anoFinal
    ){
       
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("parampAnoInicial", anoInicial.toString());
        paramsMap.put("parampAnoFinal", anoFinal.toString());

        String url = buildEndpointUri(spoPath, targetValorAno, paramsMap);

        try {
            return extractDataFromResponse(doRequest(url));
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }

}
