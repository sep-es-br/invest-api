package br.gov.es.invest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.model.DataMock;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.CustoService;
import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import lombok.RequiredArgsConstructor;


@RestController
@CrossOrigin(origins = "${frontend.host}")
@RequestMapping("/controler")
@RequiredArgsConstructor
public class ControleMasterController {

    private final CustoService custoService;
    private final ObjetoService objetoService;
    private final InvestimentoService investimentoService;
    private final ExecucaoOrcamentariaService execucaoOrcamentariaService;
    private final PlanoOrcamentarioService planoOrcamentarioService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    private final FonteOrcamentariaService fonteOrcamentariaService;

    @PutMapping("gerarDB")
    public ResponseEntity<String> putMethodName() {
        
        // if(DataMock.populado) return ResponseEntity.ok("Concluido previamente");

        // DataMock.popular2();

        // custoService.saveAll(DataMock.noCustos);
        // objetoService.saveAll(DataMock.noObjetos);
        // investimentoService.saveAll(DataMock.noInvestimentos);
        // unidadeOrcamentariaService.saveAll(DataMock.noUnidadeOrcamentarias);
        // planoOrcamentarioService.saveAll(DataMock.noPlanoOrcamentarios);
        // execucaoOrcamentariaService.saveAll(DataMock.noExecucaoOrcamentarias);
        // fonteOrcamentariaService.saveAll(DataMock.noFonteOrcamentarias);

        

        return ResponseEntity.ok().body("Concluido");
    }
}
