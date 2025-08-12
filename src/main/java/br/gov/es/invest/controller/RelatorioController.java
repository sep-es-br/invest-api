package br.gov.es.invest.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.RelatorioService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    
    @GetMapping("/gerarRelatorio/{tipoDespesa}/{anoDe}/{anoAte}")
    public ResponseEntity<?> gerarRelatorio(
        @PathVariable String tipoDespesa, @PathVariable Integer anoDe, @PathVariable Integer anoAte,
        @RequestParam(required=false) String idsUnidade,@RequestParam(required=false) String idsPlanos,@RequestParam(required=false) String idFonte,
        @RequestParam(required=false) Integer gnd, @RequestParam boolean verTodasUnidades, @RequestHeader("Authorization") String authToken
        ) {
        
        
        try{
            String fileName = "relatório-detalhado-" + anoDe + "-" + anoAte + ".xlsx";



            List<String> idsUo = null;
            if(idsUnidade == null && !verTodasUnidades) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

                idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
            } else if(idsUnidade != null) {
                idsUo = new ObjectMapper().readValue(idsUnidade, new TypeReference<List<String>>(){});
            }
            
            List<String> idPlanoList = idsPlanos == null ? null
                        : new ObjectMapper().readValue(idsPlanos, new TypeReference<List<String>>(){});
            
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (Workbook workbook = relatorioService.gerarPlanilha(tipoDespesa, idsUo, idPlanoList, idFonte, gnd, anoDe, anoAte)) {
                workbook.write(outputStream);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, exception.getLocalizedMessage(), exception);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar planilha", Arrays.asList(exception.getLocalizedMessage()));
        }

        
    }

    @GetMapping("/gerarRelatorioConsolidado/{tipoDespesa}/{anoDe}/{anoAte}")
    public ResponseEntity<?> gerarRelatorioConsolidado(
        @PathVariable String tipoDespesa, @PathVariable Integer anoDe, @PathVariable Integer anoAte,
        @RequestParam(required=false) String idsUnidade,@RequestParam(required=false) String idFonte,
        @RequestParam(required=false) Integer gnd, @RequestParam boolean verTodasUnidades, @RequestHeader("Authorization") String authToken
        ) {
        
        
        try{
            String fileName = "relatório-consolidado-" + anoDe + ".xlsx";

            List<String> idsUo = null;
            if(idsUnidade == null && !verTodasUnidades) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

                idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
            } else if(idsUnidade != null) {
                idsUo = new ObjectMapper().readValue(idsUnidade, new TypeReference<List<String>>(){});
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (Workbook workbook = relatorioService.gerarPlanilhaConsolidado(tipoDespesa, idsUo, idFonte, gnd, anoDe, anoAte)) {
                workbook.write(outputStream);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, exception.getLocalizedMessage(), exception);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar planilha", Arrays.asList(exception.getLocalizedMessage()));
        }

    }

    @GetMapping("/valoresTotalizadosConsolidado/{tipoDespesa}/{anoDe}/{anoAte}")
    public ResponseEntity<?> getValoresConsolidado(
        @PathVariable String tipoDespesa, @PathVariable Integer anoDe, @PathVariable Integer anoAte,
        @RequestParam(required=false) String idsUnidade,@RequestParam(required=false) String idFonte,
        @RequestParam(required=false) Integer gnd, @RequestParam boolean verTodasUnidades, @RequestHeader("Authorization") String authToken
        ) {
            
        try{
            
            List<String> idsUo = null;
            if(idsUnidade == null && !verTodasUnidades) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

                idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
            } else if(idsUnidade != null) {
                idsUo = new ObjectMapper().readValue(idsUnidade, new TypeReference<List<String>>(){});
            }


            return ResponseEntity.ok(relatorioService.cardsTotaisRelatorioConsolidado(tipoDespesa, idsUo, idFonte, gnd, anoDe, anoAte));
        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, exception.getLocalizedMessage(), exception);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar Totalização", Arrays.asList(exception.getLocalizedMessage()));
        }
    }
    
    


}
