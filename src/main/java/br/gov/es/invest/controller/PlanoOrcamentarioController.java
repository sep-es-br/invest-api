package br.gov.es.invest.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ObjetoDTO;
import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/plano")
@RequiredArgsConstructor
public class PlanoOrcamentarioController {

    @Value("${frontend.host}")
    private String frontHost;

    private final Logger logger = Logger.getLogger("PlanoOrcamentarioController");

    private final PlanoOrcamentarioService service;

    @GetMapping("/all")
    public ResponseEntity<List<PlanoOrcamentarioDTO>> getAllByFiltro() {

        ArrayList<PlanoOrcamentarioDTO> planosDTO = new ArrayList<>();

        service.getAllSimples().forEach(plano -> planosDTO.add(new PlanoOrcamentarioDTO(plano.getId(), plano.getCodigo())));

        return ResponseEntity.ok(planosDTO);
        

    }
    
}