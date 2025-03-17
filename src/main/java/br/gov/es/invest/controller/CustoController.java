package br.gov.es.invest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.service.CustoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/custo")
@RequiredArgsConstructor
public class CustoController {

    private final CustoService service;

    
    
}
