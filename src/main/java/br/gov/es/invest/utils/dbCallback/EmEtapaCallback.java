/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.dbCallback;

import br.gov.es.invest.model.EmEtapa;
import java.time.ZonedDateTime;
import org.springframework.data.neo4j.core.mapping.callback.BeforeBindCallback;

/**
 *
 * @author gean.carneiro
 */
public class EmEtapaCallback implements BeforeBindCallback<EmEtapa> {

    @Override
    public EmEtapa onBeforeBind(EmEtapa entity) {
        
        if(entity.getTimestamp() == null) {
            entity.setTimestamp(ZonedDateTime.now());
        }
        
        return entity;
        
    }
    
    
    
}
