package br.gov.es.invest.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeOrcamentariaController {

    @Value("${frontend.host}")
    private String frontHost;

    private final Logger logger = Logger.getLogger("PlanoOrcamentarioController");

    private final UnidadeOrcamentariaService service;

    @GetMapping("/all")
    public ResponseEntity<List<UnidadeOrcamentariaDTO>> getAllByFiltro() {

        List<UnidadeOrcamentariaDTO> planosDTO = service.getAll().stream().map(unidade -> new UnidadeOrcamentariaDTO(unidade)).toList();

        return ResponseEntity.ok(planosDTO);
        

    }
    
}