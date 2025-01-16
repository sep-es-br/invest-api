package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.AreaTematicaDto;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.service.AreaTematicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/areaTematica")
@RequiredArgsConstructor
public class AreaTematicaController {
    
    private final AreaTematicaService service;


    @GetMapping("")
    public ResponseEntity<?> findAreaTematica(@RequestParam(required = false) String areaTematicaId) {
        
        if(areaTematicaId == null) {

            List<AreaTematicaDto> listDto = service.findAll().stream()
                                            .map(areaTematica -> new AreaTematicaDto(areaTematica))
                                            .toList();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(listDto);


        }
        
        return MensagemErroRest.asResponseEntity(
            HttpStatus.NOT_IMPLEMENTED, 
            "Função não implementada Ainda", 
            null
        );
        
    }
    


}
