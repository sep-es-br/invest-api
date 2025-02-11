package br.gov.es.invest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.FonteOrcamentariaDTO;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.service.FonteOrcamentariaBIService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/fonte")
@RequiredArgsConstructor
public class FonteOrcamentariaController {
    
    private final FonteOrcamentariaService service;
    private final FonteOrcamentariaBIService biService;

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
    

    @GetMapping("/doSigefes")
    public List<FonteOrcamentariaDTO> getDoSigefes () {

        ArrayList<FonteOrcamentaria> fontes = new ArrayList<>(service.findFontesExtras()) ;

        fontes.addAll(biService.getFontes());

        return fontes.stream()
            .map(fonte -> new FonteOrcamentariaDTO(fonte))
            .sorted((fonte1, fonte2) -> fonte1.getCodigo().compareTo(fonte2.getCodigo()))
            .toList();
    }
    
}
