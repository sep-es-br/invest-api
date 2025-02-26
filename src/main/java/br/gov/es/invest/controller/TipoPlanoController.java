package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.TipoPlanoDto;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.service.TipoPlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/tipoPlano")
@RequiredArgsConstructor
public class TipoPlanoController {
    

    private final TipoPlanoService service;

    @GetMapping("")
    public ResponseEntity<?> findBy(@RequestParam(required = false) String id, @RequestParam(required = false) String sigla) {
        if(id == null && sigla == null) {
            return ResponseEntity.ok(
                service.findAll().stream()
                .map(tipoPlano -> new TipoPlanoDto(tipoPlano))
                .toList()
            );
        }
        Optional<TipoPlano> optTipoPlano = null;

        if(id != null) {
            optTipoPlano = service.findById(id);
        } else {
            optTipoPlano = service.findBySigla(sigla);
        }

        if(optTipoPlano.isPresent()) {
            return ResponseEntity.ok(new TipoPlanoDto(optTipoPlano.get()));
        } else {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NOT_FOUND, 
                "Tipo do plano não encontrado", 
                Arrays.asList("não foi possivel encontrar um tipo plano que atenda a busca: id = " + id + "; sigla = " + sigla)
            );
        }

        
    }
    



}
