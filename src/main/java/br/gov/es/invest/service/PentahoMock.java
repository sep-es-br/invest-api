package br.gov.es.invest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PentahoMock extends PentahoBIService {
    
    private final String arquivosPath = "src/main/resources/ArquivosMock/";

    private final String cardsTotais = arquivosPath + "cardsTotais.result.txt";
    private final String valorAno = arquivosPath + "valorAno.result.txt";
    private final String valorMes = arquivosPath + "valorMes.result.txt";

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

    private List<Map<String, JsonNode>> extrairDados(String path) {
        
        return extractDataFromResponse(getFileContent(path));


    }

    public List<Map<String, JsonNode>> getCardsTotais(
        String codFonte, Integer exercicio, String codUnidade, String codPlano
    ){
        return extrairDados(cardsTotais);
    }

    public List<Map<String, JsonNode>> getDadosPorMes(
        Integer anoInicial, Integer anoFinal
    ){
        return extrairDados(valorMes);
    }

    public List<Map<String, JsonNode>> getDadosPorAno(
        Integer anoInicial, Integer anoFinal
    ){
        return extrairDados(valorAno);
    }

}
