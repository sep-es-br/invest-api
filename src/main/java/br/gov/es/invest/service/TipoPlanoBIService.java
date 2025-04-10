package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.biClient.BiClient;
import br.gov.es.invest.dto.acessocidadaoapi.LoginACResponseDto;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.repository.TipoPlanoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPlanoBIService {
    
    private final BiClient biClient;
    private final TipoPlanoService tipoPlanoService;



    public List<TipoPlano> findTiposPlano(String codPo) {
        List<Map<String, JsonNode>> biList = biClient.findTiposPlano(codPo);

        return biList.stream()
                .filter(map -> !map.get("tipo").asText().equals("ND"))
                .map(map -> TipoPlano.builder()
                                .id(tipoPlanoService.getIdBySigla(map.get("tipo").asText()))
                                .nome(map.get("nome").asText())
                                .sigla(map.get("tipo").asText())
                                .build()
                )
                .collect(Collectors.toList());
    }
    
    

}
