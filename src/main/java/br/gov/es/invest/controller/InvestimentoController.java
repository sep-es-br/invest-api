package br.gov.es.invest.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.InvestimentoDTO;
import br.gov.es.invest.dto.InvestimentoFiltroDTO;
import br.gov.es.invest.service.InvestimentoService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/investimento")
@RequiredArgsConstructor
public class InvestimentoController {


    private final InvestimentoService service;

    private final Logger logger = Logger.getLogger("InvestimentoController");

    @GetMapping("/all")
    public ResponseEntity<List<InvestimentoDTO>> getTotalPrevisto(@RequestParam String filtroJson) {
        try {
            InvestimentoFiltroDTO filtroDTO = new ObjectMapper().readValue(filtroJson, InvestimentoFiltroDTO.class);

            List<InvestimentoDTO> investimentosDTO = service.findAllByFilter(filtroDTO).stream()
                .map(inv -> new InvestimentoDTO(inv)).toList();
            return ResponseEntity.ok(investimentosDTO);
        } catch (Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

        
    }
    
}
