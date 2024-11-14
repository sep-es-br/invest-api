package br.gov.es.invest.controller;

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
import br.gov.es.invest.service.ObjetoService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/objeto")
@RequiredArgsConstructor
public class ObjetoController {

    @Value("${frontend.host}")
    private String frontHost;

    private final Logger logger = Logger.getLogger("ObjetoController");

    private final ObjetoService service;

    @GetMapping("/all")
    public ResponseEntity<List<ObjetoDTO>> getAllByFiltro(@RequestParam String filtroJson) {

        try{
            ObjetoFiltroDTO filtroDTO = new ObjectMapper().readValue(filtroJson, ObjetoFiltroDTO.class);

            List<ObjetoDTO> objetosDTO = service.getAllByFilter(filtroDTO).stream().map(obj -> new ObjetoDTO(obj)).toList();

            // List<ObjetoDTO> objetosDTO = Arrays.asList();

            return ResponseEntity.ok(objetosDTO);
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam String exercicio, @RequestParam int numPag, @RequestParam int qtPorPag
    ) {
        return ResponseEntity.ok(service.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio));
    }
    
}
