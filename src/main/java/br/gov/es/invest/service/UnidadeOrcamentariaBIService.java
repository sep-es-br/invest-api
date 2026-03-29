package br.gov.es.invest.service;

import br.gov.es.invest.feignClient.BiClient;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.model.UnidadeOrcamentaria;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadeOrcamentariaBIService extends PentahoBIService {
    @Value("${pentahoBI.spo.path}") // /public/dashboard/spo
    private String spoPath;

    @Value("${pentahoBI.spo.unidadesOrcamentarias}")
    private String unidadesTarget;
    
    @Value("${pentahoBI.spo.resouce.unidadeOrcamentaria}")
    private String resourceUnidadeOrcamentaria;

    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    
    private final BiClient biClient;

    public List<UnidadeOrcamentaria> getTodasUnidades(String codOrgao){
       
        try {
            List<Map<String, JsonNode>> dados = biClient.doQuery(resourceUnidadeOrcamentaria, Map.of("parampCodOrgao", Optional.ofNullable(codOrgao).orElse("todos")));
            
            List<UnidadeOrcamentaria> unidades = dados.stream()
            .filter( dado -> !dado.get("cod_uo").asText().startsWith("0") &&  !dado.get("cod_uo").asText().startsWith("8"))
            .map(
                dado -> UnidadeOrcamentaria.builder()
                        .id(unidadeOrcamentariaService.getIdByCod(dado.get("cod_uo").asText()))
                        .codigo(dado.get("cod_uo").asText())
                        .sigla(dado.get("sigla_uo").asText())
                        .nome(dado.get("nome_uo").asText())
                        .build()
            ).collect(Collectors.toList());

            return unidades;
        } catch (Exception ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return Arrays.asList();
        }
    }
}
