package br.gov.es.invest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.StatusDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    
    private final StatusService statusService;

    @GetMapping("")
    public ResponseEntity<?> getStatus(@RequestParam(required = false) String id) {
        
        if(id == null) {
            return ResponseEntity.ok().body(statusService.findAllStatusObjeto().stream().map(StatusDTO::parse).toList());
        }

        return MensagemErroRest.asResponseEntity(
            HttpStatus.NOT_IMPLEMENTED, 
            "Função ainda não implementada", 
            null
        );
    }
    

}
