package br.gov.es.invest.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/execucao")
@RequiredArgsConstructor
public class ExecucaoOrcamentariaController {
    
    
    private final ExecucaoOrcamentariaService service;

    @GetMapping("/totalOrcado")
    public Double getTotalOrcado(@RequestParam String ano) {
        return service.getTotalOrcadoByAno(ano);
    }
}
