package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.LocalidadeDto;
import br.gov.es.invest.model.Localidade;
import br.gov.es.invest.service.LocalidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/localidade")
@RequiredArgsConstructor
public class LocalidadeController {
    
    private final LocalidadeService service;

    @GetMapping("")
    public List<LocalidadeDto> getLocalidade() {
        

        return this.service.findAll().stream().map(loc -> new LocalidadeDto(loc)).toList();
    }
    

}
