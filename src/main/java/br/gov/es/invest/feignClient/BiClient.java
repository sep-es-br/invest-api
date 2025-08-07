package br.gov.es.invest.feignClient;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.config.feign.pentahoInterceptorConfig;

@FeignClient(
    name="biClient", 
    url="${pentahoBI.url}",
    configuration=pentahoInterceptorConfig.class
)
public interface BiClient {
    
    @GetMapping("/plugin/cda/api/doQuery?path=/public/dashboard/spo/spo_tipo_plano.cda&dataAccessId=spo_tipo_plano")
    public List<Map<String, JsonNode>> findTiposPlano(@RequestParam("parampCodPO") String codPo);
       
    @GetMapping("/plugin/cda/api/doQuery?path=/public/dashboard/spo/{resource}.cda&dataAccessId={resource}")
    public List<Map<String, JsonNode>> doQuery(@PathVariable String resource, @RequestParam Map<String, String> params);
    
    

}
