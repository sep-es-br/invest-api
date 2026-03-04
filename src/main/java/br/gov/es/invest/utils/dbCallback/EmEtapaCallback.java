/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.dbCallback;

import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.service.UsuarioService;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.neo4j.driver.types.MapAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.mapping.Neo4jPersistentEntity;
import org.springframework.data.neo4j.core.mapping.callback.AfterConvertCallback;
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
