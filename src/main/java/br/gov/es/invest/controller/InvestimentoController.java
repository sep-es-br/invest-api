package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.dto.InvestimentoTiraDTO;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/investimento")
@RequiredArgsConstructor
public class InvestimentoController {


    private final InvestimentoService service;

    private final Logger logger = Logger.getLogger("InvestimentoController");

    @GetMapping("/all")
    public ResponseEntity<List<InvestimentoTiraDTO>> getAllByFilter(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
            @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte, @RequestParam int numPag, @RequestParam int qtPorPag
        ) {
            try {
                List<InvestimentoTiraDTO> investimentosDTO = service.findAllByFilter(
                        nome, codUnidade, codPO, exercicio, idFonte, PageRequest.of(numPag-1, qtPorPag)
                    ).stream()
                    .map(inv -> new InvestimentoTiraDTO(inv)).toList();

                return ResponseEntity.ok(investimentosDTO);
            } catch (Exception e){
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
                return ResponseEntity.internalServerError().build();
            }
        
    }
    
    @GetMapping("/allTira")
    public ResponseEntity<List<InvestimentoTiraDTO>> getAllTiraByFilter(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
            @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte, @RequestParam int numPag, @RequestParam int qtPorPag
        ) {
            try {

                List<Investimento> investimentos = service.findAllByFilter(
                    nome, codUnidade, codPO, exercicio, idFonte, PageRequest.of(numPag-1, qtPorPag)
                );

                List<InvestimentoTiraDTO> investimentosDTO = investimentos.stream()
                    .map(inv -> new InvestimentoTiraDTO(inv)).toList();

                return ResponseEntity.ok(investimentosDTO);
            } catch (Exception e){
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
                return ResponseEntity.internalServerError().build();
            }
        
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam String exercicio, @RequestParam(required = false) String idFonte
    ) {
        return ResponseEntity.ok(service.ammountByFilter(nome, codUnidade, codPO, exercicio, idFonte));
    }
    
    
}
