/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.service;

import br.gov.es.invest.feignClient.SpoClient;
import br.gov.es.invest.feignClient.dto.PageResponseDto;
import br.gov.es.invest.feignClient.dto.PropostaRequest;
import br.gov.es.invest.feignClient.dto.PropostaResponse;
import br.gov.es.invest.utils.DataListResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
@RequiredArgsConstructor
public class AudienciaPublicaService {
    
    private final SpoClient spoClient;
    
    private final ObjetoService objSrv;
    
    public PageResponseDto<PropostaResponse> listaAudienciaPublica(
            List<String> uos,
            String areaTematica,
            String filtroTexto,
            int pag
    ) {
        
        List<String> hashsUsados = objSrv.listarHashUsadosPorDemandaPublica();
        
        PropostaRequest request = 
                PropostaRequest.builder()
                .syncedIds(hashsUsados)
                .budgetUnitCodes(uos)
                .planItemName(areaTematica)
                .textFilter(filtroTexto)
                .pageNumber(pag)
                .pageSize(15)
                .build();
        
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        
        
        
        return spoClient.findListagemPropostas(request);

        
    }
    
    public Long idUltimaAudiencia() {
        
        return spoClient.getLastConferenceId().id();
        
    }
    
    
}
