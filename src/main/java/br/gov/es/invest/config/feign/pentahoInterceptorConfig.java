package br.gov.es.invest.config.feign;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;
import feign.ResponseInterceptor.Chain;

public class pentahoInterceptorConfig {
    
    @Value("${pentahoBI.userId}")
    public String pentahoUser;

    @Value("${pentahoBI.password}")
    public String pentahoPassw;
    
    @Bean
    public ResponseInterceptor responseInterceptor() {
        return (InvocationContext invocationContext, Chain chain) -> {
            // TODO Auto-generated method stub
            
            try (InputStream inputStream = invocationContext.response().body().asInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(inputStream);
                
                // Processamento mais direto
                List<String> labels = StreamSupport.stream(root.path("metadata").spliterator(), false)
                    .map(node -> node.path("colName").asText())
                    .collect(Collectors.toList());
                
                return StreamSupport.stream(root.path("resultset").spliterator(), false)
                    .map(node -> {
                        Map<String, JsonNode> map = new HashMap<>();
                        for (int i = 0; i < labels.size() && i < node.size(); i++) {
                            map.put(labels.get(i), node.get(i));
                        }
                        return map;
                    })
                    .collect(Collectors.toList());
            }
        };
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            // TODO Auto-generated method stub
            String notEncoded = pentahoUser + ":" + pentahoPassw;
            String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

            template.header("Authorization", encodedAuth);
            
        };
    }

}

