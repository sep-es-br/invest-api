package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.AplicarStatusDTO;
import br.gov.es.invest.dto.StatusDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    
    private final StatusService statusService;
    private final ObjetoService objetoService;

    @GetMapping("")
    public ResponseEntity<?> getStatus(@RequestParam(required = false) String id) {
        
        if(id == null) {
            return ResponseEntity.ok().body(statusService.findAll().stream().map(StatusDTO::parse).toList());
        }

        return MensagemErroRest.asResponseEntity(
            HttpStatus.NOT_IMPLEMENTED, 
            "Função ainda não implementada", 
            null
        );
    }

    @PutMapping("/aplicarStatus")
    public ResponseEntity<?> aplicarStatus(@RequestBody AplicarStatusDTO aplicarStatusDto) {
        
        Optional<Objeto> optObjeto = objetoService.getById(aplicarStatusDto.objetoId());

        if(optObjeto.isEmpty())
            return MensagemErroRest.asResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                "Objeto não encontrado", 
                Arrays.asList("Incapaz de localizar objeto com o elementId: " + aplicarStatusDto.objetoId())
            );
        
        Optional<Status> optStatus = statusService.getByStatusId(aplicarStatusDto.status());

        if(optStatus.isEmpty())
            return MensagemErroRest.asResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                "Status não encontrado", 
                Arrays.asList("Incapaz de localizar Status com o statusId: " + aplicarStatusDto.status())
            );

        statusService.aplicarStatus(optObjeto.get(), optStatus.get());

        return ResponseEntity.ok().build();
    }
    
    

}
