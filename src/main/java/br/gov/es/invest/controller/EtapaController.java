package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.EtapaDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.service.EtapaService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/etapa")
@RequiredArgsConstructor
public class EtapaController {
    
    private final EtapaService etapaService;

    @GetMapping("")
    public ResponseEntity<?> getEtapa(@RequestParam(required = false) String id) {

        if(id == null) {
            List<Etapa> etapas = etapaService.findAll();

            return ResponseEntity
                    .ok(etapas.stream().map(EtapaDTO::parse).toList());

        }

        return MensagemErroRest.asResponseEntity(
            HttpStatus.NOT_IMPLEMENTED, 
            "Funcionalidade não implementada ainda", 
            null
        );
    }
}
