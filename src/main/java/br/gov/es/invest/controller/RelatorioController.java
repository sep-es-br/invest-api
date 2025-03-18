package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import br.gov.es.invest.dto.DadosConsolidadosValores;



@RestController
@RequestMapping("/relatorio")
public class RelatorioController {
    
    @GetMapping("/consolidado/valores")
    public ResponseEntity<?> getValoresConsolidado(@RequestParam Integer ano) {
        
        List<DadosConsolidadosValores> valores;

        valores = Arrays.asList(
            new DadosConsolidadosValores("0", "Caixa", 1000d, 2000d),
            new DadosConsolidadosValores("0", "Demais", 3000d, 4000d)
        );
        
        return ResponseEntity.ok(valores);
    }
    


}
