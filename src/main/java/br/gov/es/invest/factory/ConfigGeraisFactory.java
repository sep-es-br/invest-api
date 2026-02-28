/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.configGerais.ConfigGeraisFormDto;
import br.gov.es.invest.model.ConfigGerais;
import br.gov.es.invest.service.ConfigGeraisService;
import br.gov.es.invest.utils.DateTimeUtils;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class ConfigGeraisFactory {
    
    private final ConfigGeraisService srv;
    
    public ConfigGeraisFactory(
            ConfigGeraisService srv
    ) {
        this.srv = srv;
    }
    
    public ConfigGerais toModel(ConfigGeraisFormDto form) {
        
        ConfigGerais config = this.srv.getConfig();
        config.setRevisaoPip(
                Optional.ofNullable(form.inicioRevisaoPip()).map(DateTimeUtils::getZonedDateTime).orElse(null),
                Optional.ofNullable(form.fimRevisaoPip()).map(DateTimeUtils::getZonedDateTime).map(data -> data.toLocalDate().atTime(LocalTime.MAX).atZone(data.getZone())).orElse(null)
        );
        
        return config;       
        
    }
    
    public ConfigGeraisFormDto toFormDto(ConfigGerais model) {
        return new ConfigGeraisFormDto(
                Optional.ofNullable(model.getInicioRevisaoPip()).map(DateTimeUtils::formatZonedDateTime).orElse(null), 
                Optional.ofNullable(model.getFimRevisaoPip()).map(DateTimeUtils::formatZonedDateTime).orElse(null)
        );
    }
}
