package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.FonteOrcamentariaDTO;
import br.gov.es.invest.service.FonteOrcamentariaService;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/fonte")
@RequiredArgsConstructor
public class FonteOrcamentariaController {
    
    private final FonteOrcamentariaService service;

    @GetMapping("")
    public List<FonteOrcamentariaDTO> findAll() {
        return service.findAll().stream().map(fonte -> new FonteOrcamentariaDTO(fonte)).sorted((f1, f2) -> f1.getCodigo().compareTo(f2.getCodigo())).toList();
    }

    @GetMapping("/extras")
    public List<FonteOrcamentariaDTO> getFontesExtras() {
        return service.findFontesExtras().stream()
                .map(FonteOrcamentariaDTO::parse)
                .toList();
    }
    
    
}
