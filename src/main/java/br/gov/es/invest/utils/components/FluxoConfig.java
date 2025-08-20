/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.components;

import br.gov.es.invest.utils.domains.Acao;
import br.gov.es.invest.utils.domains.Fluxo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class FluxoConfig {
    
    public static final String FLUXO_AVALIACAO_PIP = "avaliacaoPip";
       
    private final List<Fluxo> fluxos;

    public FluxoConfig(@Value("${fluxoConfigPath}") String fluxoPath, ObjectMapper mapper) throws IOException{
        this.fluxos = mapper.readValue(new File(fluxoPath), new TypeReference<List<Fluxo>>(){});
    }

    public List<Fluxo> getFluxos() {
        return this.fluxos;
    }
    
    public Fluxo getFluxo(String fluxoId) {
        List<Fluxo> result = this.fluxos.stream().filter(f -> f.fluxoId().equals(fluxoId)).toList();
        
        return !result.isEmpty() ? result.get(0) : null;
    }
        
    
}
