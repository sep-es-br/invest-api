package br.gov.es.invest.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.SetorDto;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import br.gov.es.invest.service.ACService;
import br.gov.es.invest.service.InfosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/infos")
@RequiredArgsConstructor
public class InfosController {

    @Value("${frontend.host}")
    private String frontHost;

    private final InfosService service;
    private final ACService aCService;

    @GetMapping("/allAnos")
    public ResponseEntity<List<String>> getTodosAnos() {

        return ResponseEntity.ok(service.getAllAnos());

    }

    @GetMapping("/iconesDisponiveis")
    public List<String> getIconesDisponiveis() {
        
       return service.getIconesDisponiveis();
    }

    @GetMapping("/unidades")
    public List<OrgaoDto> getUnidades() {
        return aCService.getOrgaos().stream().map(orgao -> new OrgaoDto(orgao)).toList();
    }
    
    @GetMapping("/setores")
    public List<SetorDto> getSetores(@RequestParam String unidadeGuid) {
        return aCService.getSetores(unidadeGuid);
    }
    
    @GetMapping("/papeis")
    public List<PapelDto> getPapeis(@RequestParam String setorGuid) {
        return aCService.getPapeis(setorGuid);
    }
    

    
}

