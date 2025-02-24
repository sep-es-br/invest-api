package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import br.gov.es.invest.dto.ContaTiraDTO;
import br.gov.es.invest.dto.InvestimentoTiraDTO;
import br.gov.es.invest.dto.projection.TiraInvestimentoProjection;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.utils.DataListResult;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/investimento")
@RequiredArgsConstructor
public class InvestimentoController {


    private final InvestimentoService service;

    private final ObjetoService objetoService;
    private final ContaService contaService;

    private final Logger logger = Logger.getLogger("InvestimentoController");

    // @GetMapping("/all")
    // public ResponseEntity<List<InvestimentoTiraDTO>> getAllByFilter(
    //         @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
    //         @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte, @RequestParam int numPag, @RequestParam int qtPorPag
    //     ) {
    //         try {
    //             List<InvestimentoTiraDTO> investimentosDTO = service.findAllByFilter(
    //                     nome, codUnidade, codPO, exercicio, idFonte, PageRequest.of(numPag-1, qtPorPag)
    //                 ).stream()
    //                 .map(inv -> new InvestimentoTiraDTO(inv)).toList();

    //             return ResponseEntity.ok(investimentosDTO);
    //         } catch (Exception e){
    //             logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
    //             return ResponseEntity.internalServerError().build();
    //         }
        
    // }
    
    @GetMapping("/filtrarValores")
    public ResponseEntity<?> getAllTiraByFilter(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
            @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte, @RequestParam int numPag, @RequestParam int qtPorPag,
            @RequestParam(required = false) Integer gnd
        ) {
            try{
            List<String> idsUo = codUnidade == null ? null : new JsonMapper().readValue(codUnidade, new TypeReference<List<String>>() {});
            List<String> idsPo = codPO == null ? null : new JsonMapper().readValue(codPO, new TypeReference<List<String>>() {});
        
        
        DataListResult<TiraInvestimentoProjection> dataList = service.findAllTiraBy(nome, idsUo, idsPo, exercicio, idFonte, gnd, PageRequest.of(numPag-1, qtPorPag));
        
        
        DataListResult<InvestimentoTiraDTO> dataListDto = new DataListResult<>(
            dataList.data().stream().map(investimento -> {
                return InvestimentoTiraDTO.parse(investimento, objetoService.findObjetoCadastradoByContaBy(investimento.id(), exercicio, idFonte, gnd, null));
            }).toList(), 
            dataList.ammount()
        );

        return ResponseEntity.ok(dataListDto);
            }
            catch( Exception ex) {
                Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "erro ao buscar investimentos", Arrays.asList(ex.getLocalizedMessage()));
            }
    }

    @GetMapping("/countValores")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam Integer exercicio, @RequestParam(required = false) String idFonte
    ) {
        return ResponseEntity.ok(service.ammountByFilterValores(nome, codUnidade, codPO, exercicio, idFonte));
    }
    
    
}
