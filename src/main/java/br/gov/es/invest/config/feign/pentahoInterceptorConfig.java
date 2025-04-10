package br.gov.es.invest.config.feign;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import feign.InvocationContext;
import feign.ResponseInterceptor;
import feign.ResponseInterceptor.Chain;

@Configuration
public class pentahoInterceptorConfig {
    
    @Bean
    public ResponseInterceptor responseInterceptor() {
        return (InvocationContext invocationContext, Chain chain) -> {
            // TODO Auto-generated method stub
            
            BufferedReader reader = new BufferedReader(invocationContext.response().body().asReader(Charset.forName("UTF-8")));

            String json = "";
            String line = reader.readLine();
            while(line != null){
                json += line;

                line = reader.readLine();
            }

            
            ArrayList<Map<String, JsonNode>> lista = new ArrayList<>();
            
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
            return lista;
        };
    }

}
