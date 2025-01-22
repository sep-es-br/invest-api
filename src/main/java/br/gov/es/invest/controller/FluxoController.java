package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.FluxoDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.service.FluxoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/fluxo")
@RequiredArgsConstructor
public class FluxoController {
    
    private final FluxoService fluxoService;

    @GetMapping("")
    public ResponseEntity<?> find(@RequestParam(required = false) String id) {

        if(id == null) {
            List<FluxoDTO> fluxoDTOs = fluxoService.findAll().stream()
                                    .map(fluxo -> new FluxoDTO(fluxo))
                                    .toList();

            return ResponseEntity.ok(fluxoDTOs);
        }

        return MensagemErroRest.asResponseEntity(HttpStatus.NOT_IMPLEMENTED, "Recurso não implementado ainda", null);
    }
    

}
