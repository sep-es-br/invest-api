package br.gov.es.invest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.CampoDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.service.CampoService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/campo")
@RequiredArgsConstructor
public class CampoController {
    
    public final CampoService campoService;

    @GetMapping("")
    public ResponseEntity<?> findBy(@RequestParam(required = false) String id){

        if(id == null){
            return ResponseEntity.ok(campoService.findAll().stream().map(CampoDTO::parse).toList());
        } else {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NOT_IMPLEMENTED, 
                "Recurso não implementado", 
                null
            );
        }


    }

}
