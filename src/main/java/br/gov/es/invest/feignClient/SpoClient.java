/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.feignClient;

import br.gov.es.invest.config.feign.pentahoInterceptorConfig;
import br.gov.es.invest.feignClient.dto.ParticipeProposalListRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author gean.carneiro
 */
@FeignClient(
    name="spoClient", 
    url="${participe.apiUrl}"
)
public interface SpoClient {
    
    @PostMapping("/integration/spo/proposalsList")
    public feign.Response findListagemPropostas(@RequestBody ParticipeProposalListRequestDto request);
     
}
