package br.gov.es.invest.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import br.gov.es.invest.dto.desserializer.GoogleIconsDeserializer;
import br.gov.es.invest.model.Ano;
import br.gov.es.invest.repository.AnoRepository;
import br.gov.es.invest.repository.CustoRepository;
import br.gov.es.invest.repository.ExecucaoOrcamentariaRepository;

@Service
public class InfosService {
    

    @Autowired
    private AnoRepository anoRepository;

    public List<String> getAllAnos() {

        List<Ano> anos = anoRepository.findAll(Sort.by("ano"));


        return anos.stream().map(ano -> ano.getAno()).toList();
    }

    public List<String> getIconesDisponiveis() {
         HttpURLConnection con = null;
        try {
            URL googleIcons = new URL("https://fonts.google.com/metadata/icons?key=material_symbols&incomplete=true");

            con = (HttpURLConnection) googleIcons.openConnection();
            con.setRequestMethod("GET");

            if(con.getResponseCode() == HttpStatus.OK.value()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    String respLine;
                    StringBuffer response = new StringBuffer();
                    reader.readLine(); // "queima" a primeira linha que é lixo
                    while((respLine = reader.readLine()) != null) {
                        response.append(respLine);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule();
                    module.addDeserializer(List.class, new GoogleIconsDeserializer());
                    mapper.registerModule(module);

                    return (List<String>) mapper.readValue(response.toString(), List.class);
                    
                    
                }
            }

        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
        
        
        return Arrays.asList();
    }





}
