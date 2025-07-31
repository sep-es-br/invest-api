package br.gov.es.invest.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ContaTiraDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.ObjetoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
public class ContaController {


    private final ContaService service;
    private final ObjetoService objetoService; 

    private static final Logger logger = Logger.getLogger("InvestimentoController");
    
    @GetMapping("/contaTira")
    public ResponseEntity<?> getAllTiraByFilter(
            @RequestParam(required = false) String nome, @RequestParam(required = false) Long codUnidade, @RequestParam(required = false) Long codPO,
            @RequestParam Integer exercicio, @RequestParam(required = false) Long idFonte, @RequestParam int numPag, @RequestParam int qtPorPag
        ) {
            try {

                List<Conta> investimentos = service.findByFiltro(
                    nome, codUnidade, codPO, exercicio, idFonte, PageRequest.of(numPag-1, qtPorPag)
                ).stream().filter(conta -> conta.getPlanoOrcamentario() != null).toList();

                List<ContaTiraDTO> investimentosDTO = investimentos.stream()
                    .map(inv -> {
                        List<Objeto> objetos = objetoService.findObjetoByContaFiltrado(inv, exercicio, idFonte);
                        
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

    @GetMapping("{tipoDespesa}/dadosDetalhados/{exercicio}")
    public ResponseEntity<?> getDadosDetalhados (
        @PathVariable String tipoDespesa, @PathVariable Integer exercicio, 
        @RequestParam(required=false) Integer gnd, @RequestParam(required=false) String idFonte, @RequestParam Integer pag,
        @RequestParam Integer pagSize, @RequestParam(required=false) String idsUnidade, @RequestParam(required=false) String idsPlanos
    ){
        
        try {
            
            List<Long> idsUnidadeList = idsUnidade == null ? null
                    : new ObjectMapper().readValue(idsUnidade, new TypeReference<List<Long>>(){});

            List<Long> idsPlanosList = idsPlanos == null ? null
            : new ObjectMapper().readValue(idsPlanos, new TypeReference<List<Long>>(){});

            return ResponseEntity.ok(
                service.getDadosDetalhados(tipoDespesa, gnd, exercicio, idFonte, PageRequest.of(pag-1, pagSize), idsUnidadeList, idsPlanosList)
            );

        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Erro ao filtrar os dados", ex);
            return  MensagemErroRest.asResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao filtrar os dados", 
                Arrays.asList(ex.getLocalizedMessage()));
        }

        
    }

    @GetMapping("{tipoDespesa}/dadosConsolidado/{exercicioInicio}/{exercicioFim}")
    public ResponseEntity<?> getDadosConsolidados (
        @PathVariable String tipoDespesa, @PathVariable Integer exercicioInicio, @PathVariable Integer exercicioFim, 
        @RequestParam(required=false) Integer gnd, @RequestParam(required=false) String idFonte, @RequestParam Integer pag,
        @RequestParam Integer pagSize, @RequestParam(required=false) String idsUnidade
    ){
        
        try {
            
            List<Long> idsUnidadeList = idsUnidade == null ? null
                    : new ObjectMapper().readValue(idsUnidade, new TypeReference<List<Long>>(){});

            return ResponseEntity.ok(
                service.getDadosConsolidados(tipoDespesa, gnd, exercicioInicio, exercicioFim, idFonte, PageRequest.of(pag-1, pagSize), idsUnidadeList)
            );

        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Erro ao filtrar os dados", ex);
            return  MensagemErroRest.asResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao filtrar os dados", 
                Arrays.asList(ex.getLocalizedMessage()));
        }

        
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) Long codUnidade, @RequestParam(required = false) Long codPO,
        @RequestParam Integer exercicio, @RequestParam(required = false) Long idFonte
    ) {
        return ResponseEntity.ok(service.countByFilter(nome, codUnidade, codPO, exercicio, idFonte));
    }
    
    
}
