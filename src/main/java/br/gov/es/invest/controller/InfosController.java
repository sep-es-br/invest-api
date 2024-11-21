package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.service.InfosService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/infos")
@RequiredArgsConstructor
public class InfosController {

    @Value("${frontend.host}")
    private String frontHost;

    private final InfosService service;

    @GetMapping("/allAnos")
    public ResponseEntity<List<String>> getTodosAnos() {

        return ResponseEntity.ok(service.getAllAnos());

    }
    
}

