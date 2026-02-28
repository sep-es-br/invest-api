package br.gov.es.invest.controller;

import br.gov.es.invest.dto.FonteOrcamentariaDTO;
import br.gov.es.invest.service.FonteOrcamentariaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fonte")
@RequiredArgsConstructor
public class FonteOrcamentariaController {
    
    private final FonteOrcamentariaService service;

    @GetMapping("")
    public List<FonteOrcamentariaDTO> findAll() {
        return service.findAll().stream().map(fonte -> new FonteOrcamentariaDTO(fonte)).sorted((f1, f2) -> f1.getCodigo().compareTo(f2.getCodigo())).toList();
    }
    
    @GetMapping("/byCodigo/{codFonte}")
    public ResponseEntity<?> findByCodigo(
            @PathVariable String codFonte
    ) {
        return ResponseEntity.of(service.findByCodigo(codFonte).map(FonteOrcamentariaDTO::parse));
    }

    @GetMapping("/extras")
    public List<FonteOrcamentariaDTO> getFontesExtras() {
        return service.findFontesExtras().stream()
                .map(FonteOrcamentariaDTO::parse)
                .toList();
    }
    
    
}
