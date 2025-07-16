/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.service;

import br.gov.es.invest.feignClient.SpoClient;
import br.gov.es.invest.feignClient.dto.ParticipeProposalListRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
@RequiredArgsConstructor
public class AudienciaPublicaService {
    
    private final SpoClient spoClient;
    
    
    public String listaAudienciaPublica() {
        
        feign.Response resp = spoClient.findListagemPropostas(
                ParticipeProposalListRequestDto.builder()
                .year(2025)
                .build()
        );
        
        return null;
        
    }
    
    
}
