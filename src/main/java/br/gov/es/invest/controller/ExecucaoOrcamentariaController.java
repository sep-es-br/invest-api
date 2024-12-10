package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import br.gov.es.invest.service.InvestimentosBIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/execucao")
@RequiredArgsConstructor
public class ExecucaoOrcamentariaController {
    
    
    private final ExecucaoOrcamentariaService service;
    private final InvestimentosBIService investimentosBIService;

    @GetMapping("/totalOrcado")
    public Double getTotalOrcado(@RequestParam String ano) {
        return service.getTotalOrcadoByAno(ano);
    }

    @GetMapping("/importarPentaho")
    public String importarPentaho(@RequestParam(required = false) Integer anoRef) {

        // se não receber o ano de Referencia, considera o ano corrente
        if(anoRef == null) {
            anoRef = LocalDate.now().getYear();
        }

        // Carrega os dados do pentaho
        List<Map<String, JsonNode>> dadosPorMês = investimentosBIService.getDadosPorMes(anoRef-1, anoRef);
        List<Map<String, JsonNode>> dadosPorAno = investimentosBIService.getDadosPorAno(anoRef, anoRef+1);

        // processa os dados
        ArrayList<ExecucaoOrcamentaria> execucoesGeradas = new ArrayList<>();

        // primeiro o mais facil, dados por ano
        for(Map<String, JsonNode> dado : dadosPorAno){
            // guarda valores nas variaveis
            String codPo = null;
            String codUo = null;
            String codFonte = null;
            
        }

        System.out.println();

        return "Sucesso";

    }
}
