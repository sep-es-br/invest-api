package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.dto.ContaTiraDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.ObjetoService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
public class ContaController {


    private final ContaService service;
    private final ObjetoService objetoService; 

    private final Logger logger = Logger.getLogger("InvestimentoController");
    
    @GetMapping("/contaTira")
    public ResponseEntity<?> getAllTiraByFilter(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
            @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte, @RequestParam int numPag, @RequestParam int qtPorPag
        ) {
            try {

                List<Conta> investimentos = service.findByFiltro(
                    nome, codUnidade, codPO, exercicio, idFonte, PageRequest.of(numPag-1, qtPorPag)
                ).stream().filter(conta -> conta.getPlanoOrcamentario() != null).toList();

                List<ContaTiraDTO> investimentosDTO = investimentos.stream()
                    .map(inv -> {
                        List<Objeto> objetos = objetoService.findObjetoByConta(inv);
                        
                        return new ContaTiraDTO(inv, objetos);
                    }).toList();

                return ResponseEntity.ok(investimentosDTO);
            } catch (Exception e){
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
                return MensagemErroRest.asResponseEntity(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Ocorreu um erro desconhecido", 
                    Arrays.asList(e.getLocalizedMessage())
                );
            }
        
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte
    ) {
        return ResponseEntity.ok(service.countByFilter(nome, codUnidade, codPO, exercicio, idFonte));
    }
    
    
}
