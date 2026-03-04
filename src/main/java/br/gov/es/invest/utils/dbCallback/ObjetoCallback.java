/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.dbCallback;

import br.gov.es.invest.model.Objeto;
import java.time.ZonedDateTime;
import org.springframework.data.neo4j.core.mapping.callback.BeforeBindCallback;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class ObjetoCallback implements BeforeBindCallback<Objeto> {

    @Override
    public Objeto onBeforeBind(Objeto obj) {
        if(obj.getTimestamp() == null) {
            obj.setTimestamp(ZonedDateTime.now());
        }
        
        return obj;
    }
    
    
    
}
