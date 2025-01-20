package br.gov.es.invest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public abstract class PentahoBIService {
    private final Logger LOGGER = Logger.getLogger(PentahoBIService.class.getSimpleName());
    
    private static final String CHARSET = "UTF-8";

    @Value("${pentahoBI.baseURL}")
    private String baseURL;

    @Value("${pentahoBI.userId}")
    private String userId;

    @Value("${pentahoBI.password}")
    private String password;


    private String getFileContent(String path){
        StringBuilder sb = new StringBuilder();

        ClassPathResource res = new ClassPathResource(path);

        File file = new File(res.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = br.readLine();
            while(line != null){
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
       

        } catch(IOException ioException) {
            Logger.getGlobal().info(file.getAbsolutePath());
            Logger.getGlobal().log(Level.SEVERE, ioException.getLocalizedMessage(), ioException);
            return "";
        }


        return sb.toString();
    }

    protected List<Map<String, JsonNode>> extrairDados(String path) {
        
        return extractDataFromResponse(getFileContent(path));


    }

    protected String buildEndpointUri(String path, String target, Map<String, String> params) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.baseURL)
                .append("path=")
                    .append(path).append(target);

        HashMap<String, String> allParams = new HashMap<>();
        if(params != null) allParams.putAll(params);

        List<String> paramPairs = allParams.entrySet().stream()
                                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                                    .toList();
        
        strBuilder.append("&")
                    .append(String.join("&", paramPairs));

        return strBuilder.toString();
    }

    protected String doRequest(String uri) throws Exception{
        
        String notEncoded = userId + ":" + password;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodedAuth);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName(CHARSET)));

        return restTemplate.exchange(RequestEntity.get(new URI(uri)).headers(headers).build(), String.class).getBody();
    }

    protected List<Map<String, JsonNode>> extractDataFromResponse(String json) {
        ArrayList<Map<String, JsonNode>> lista = new ArrayList<>();

        try {
            JsonNode root = new ObjectMapper().readTree(json);

            ArrayNode metadata = (ArrayNode) root.get("metadata");
            ArrayList<String> labels = new ArrayList<>();
            
            metadata.forEach(node -> {
                labels.add(node.get("colName").asText());
            });

            ArrayNode resultset = (ArrayNode) root.get("resultset");

            resultset.forEach(node -> {
                ArrayNode datas = (ArrayNode) node;
                HashMap<String, JsonNode> map = new HashMap<>();
                for(int i = 0; i < datas.size(); i++) {
                    map.put(labels.get(i), datas.get(i));
                }
                lista.add(map);
            });

        } catch(Exception e) {
            LOGGER.getGlobal().severe(e.getMessage());
        }
        return lista;
    }

}
