/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.controller;

import br.gov.es.invest.service.AudienciaPublicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gean.carneiro
 */
@RestController
@RequestMapping("/audiencia-publica")
@RequiredArgsConstructor
public class AudienciaPublicaController {
    
    private final AudienciaPublicaService audienciaPublicaSrv;
    
    @GetMapping("")
    public ResponseEntity<?> getListaAudiencia(
    
    ) {
        return ResponseEntity.ofNullable(audienciaPublicaSrv.listaAudienciaPublica());
    }
    
}
