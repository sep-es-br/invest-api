package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.TipoPlanoDto;
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
    public List<TipoPlanoDto> getAll() {

        return service.findAll().stream()
            .map(tipoPlano -> new TipoPlanoDto(tipoPlano))
            .toList();
    }
    



}
