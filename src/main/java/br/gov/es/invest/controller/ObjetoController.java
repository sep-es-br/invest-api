package br.gov.es.invest.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.service.InvestimentoService;
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
    private final InvestimentoService investimentoService;

    @GetMapping("/allTira")
    public ResponseEntity<List<ObjetoTiraDTO>> getAllByFiltro(
        @RequestParam Integer exercicio,@RequestParam(required = false) String nome,
        @RequestParam(required = false) String idUnidade, @RequestParam(required = false) String idPo,@RequestParam(required = false) String status,
        @RequestParam int pgAtual, @RequestParam int tamPag 
    ) {

        try{

            List<Objeto> objetos = service.getAllByFilter(exercicio, nome, idUnidade, idPo, status, PageRequest.of(pgAtual-1, tamPag));

            List<ObjetoTiraDTO> objetosDTO = objetos.stream().map(obj -> {
                Investimento inv = investimentoService.getByObjetoId(obj.getId());
                
                return new ObjetoTiraDTO(obj, inv);
            }).toList();

            return ResponseEntity.ok(objetosDTO);
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/countInvestimentoFiltro")
    public ResponseEntity<Integer> getAmmoutByInvestimentoFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam Integer exercicio
    ) {
        return ResponseEntity.ok(service.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String idUnidade,
        @RequestParam Integer exercicio, @RequestParam(required = false) String codPO
    ) {
        return ResponseEntity.ok(service.countByFilter(nome, idUnidade, codPO, exercicio));
    }
    
}
