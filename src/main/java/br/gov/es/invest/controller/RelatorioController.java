package br.gov.es.invest.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import br.gov.es.invest.dto.DadosConsolidadosValores;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.service.RelatorioService;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;
    
    @GetMapping("/consolidado/valores")
    public ResponseEntity<?> getValoresConsolidado(@RequestParam Integer ano) {
        
        List<DadosConsolidadosValores> valores;

        valores = Arrays.asList(
            new DadosConsolidadosValores("0", "Caixa", 1000d, 2000d),
            new DadosConsolidadosValores("0", "Demais", 3000d, 4000d)
        );
        
        return ResponseEntity.ok(valores);
    }

    @GetMapping("/gerarRelatorio")
    public ResponseEntity<?> getMethodName(@RequestParam Integer anoDe, @RequestParam Integer anoAte) {
        
        
        try{
            String fileName = "relatório-detalhado-" + anoDe + "-" + anoAte + ".xls";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (Workbook workbook = relatorioService.gerarPlanilha()) {
                workbook.write(outputStream);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);

        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, exception.getLocalizedMessage(), exception);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar planilha", Arrays.asList(exception.getLocalizedMessage()));
        }

        
    }
    
    


}
