package br.gov.es.invest.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.service.ModuloService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/modulo")
@RequiredArgsConstructor
public class ModuloController {
    
    private final ModuloService service;
    
    @GetMapping("/")
    public Set<Modulo> findAll() {
        return service.findAll();
    }
    
}
