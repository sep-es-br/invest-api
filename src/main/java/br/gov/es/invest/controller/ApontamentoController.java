/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.controller;

import br.gov.es.invest.dto.ApontamentoDTO;
import br.gov.es.invest.service.ApontamentoService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gean.carneiro
 */
@RestController
@RequestMapping("/apontamento")
@RequiredArgsConstructor
public class ApontamentoController {
    
    
    private final ApontamentoService apontamentoSrv;
    
    @GetMapping("/byObjeto/{objId}")
    public ResponseEntity<?> findByObjeto(
            @PathVariable Long objId
    ) {
        return ResponseEntity.ok(
            this.apontamentoSrv.findByObjeto(objId).stream()
            .map(ApontamentoDTO::parse).collect(Collectors.toList())
        );
    }
    
    
    
}
