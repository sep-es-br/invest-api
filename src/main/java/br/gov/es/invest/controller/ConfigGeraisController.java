/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.controller;

import br.gov.es.invest.dto.configGerais.ConfigGeraisFormDto;
import br.gov.es.invest.factory.ConfigGeraisFactory;
import br.gov.es.invest.model.ConfigGerais;
import br.gov.es.invest.service.ConfigGeraisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gean.carneiro
 */
@RestController
@RequestMapping("/configGerais")
public class ConfigGeraisController {
    
    private final ConfigGeraisService configGeraisSrv;
    private final ConfigGeraisFactory configGeraisFactory;
    
    
    public ConfigGeraisController(
            ConfigGeraisService configGeraisSrv,
            ConfigGeraisFactory configGeraisFactory
    ){
        this.configGeraisSrv = configGeraisSrv;
        this.configGeraisFactory = configGeraisFactory;
    }
    
    @GetMapping({"", "/asForm"})
    public ConfigGeraisFormDto buscarConfig(){
        return this.configGeraisFactory.toFormDto(this.configGeraisSrv.getConfig());
    }
    
    @GetMapping("/checarEmRevisao")
    public Boolean emPeriodoRevisao() {
        return this.configGeraisSrv.emPeriodoRevisao();
    }
    
    @PutMapping
    public ConfigGeraisFormDto save(
            @RequestBody ConfigGeraisFormDto dto
    ) {
        
        ConfigGerais config = this.configGeraisFactory.toModel(dto);
        
        return this.configGeraisFactory.toFormDto(this.configGeraisSrv.save(config));
        
    }
    
    
}
