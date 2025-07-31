/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.feignClient;

import br.gov.es.invest.config.feign.ParticipeInterceptorConfig;
import br.gov.es.invest.feignClient.dto.PageResponseDto;
import br.gov.es.invest.feignClient.dto.PropostaRequest;
import br.gov.es.invest.feignClient.dto.PropostaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author gean.carneiro
 */
@FeignClient(
    name="spoClient", 
    url="${participe.apiUrl}",
    configuration = ParticipeInterceptorConfig.class
)
public interface SpoClient {
    
    static final String SPO_ENDPOINT_BASE = "/integration/spo";
    
    @PostMapping(SpoClient.SPO_ENDPOINT_BASE + "/proposalsList")
    public PageResponseDto<PropostaResponse> findListagemPropostas(@RequestBody PropostaRequest request);
    
    @GetMapping(SpoClient.SPO_ENDPOINT_BASE + "/lastConferenceId")
    public LastConferenceIdResp getLastConferenceId();     
    
    record LastConferenceIdResp(Long id) {}
    
    
    
    
    
     
}
