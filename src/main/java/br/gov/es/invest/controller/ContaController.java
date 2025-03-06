package br.gov.es.invest.controller;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.ObjetoService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
public class ContaController {


    private final ContaService contaService;
    private final ObjetoService objetoService; 

    private final Logger logger = Logger.getLogger("InvestimentoController");
    
   
    
    
}
