/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.service;

import br.gov.es.invest.model.ConfigGerais;
import br.gov.es.invest.repository.ConfigGeraisRepository;
import java.time.ZonedDateTime;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class ConfigGeraisService {
    
    private final ConfigGeraisRepository configGeraisRepository;
    
    public ConfigGeraisService(
            ConfigGeraisRepository configGeraisRepository
    ){
        this.configGeraisRepository = configGeraisRepository;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.configGeraisRepository.getOrCreate();
    }
    
    
    public ConfigGerais configRevisaoPip(ZonedDateTime inicio, ZonedDateTime fim) {
        
        ConfigGerais config = this.getConfig();
        
        config.setRevisaoPip(inicio, fim);
        
        return this.configGeraisRepository.save(config);
    }
    
    public boolean emPeriodoRevisao() {
        return getConfig().emPeriodoRevisao(ZonedDateTime.now());
    }
    
    public ConfigGerais save(ConfigGerais cg){
        return this.configGeraisRepository.save(cg);
    }
    
    public ConfigGerais getConfig() {
        
        return this.configGeraisRepository.getConfig().orElseThrow();
        
    }
}
