/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.AlteradoPorDto;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.AlteradoPor;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cliente
 */
@Component
@RequiredArgsConstructor
public class AlteradoPorFactory {
    
    private final UsuarioService userSrv;
    
    public AlteradoPor toModel(AlteradoPorDto dto) {
        if(dto == null) return null;
        
        Agente agente = this.userSrv.findById(dto.idAlterador()).orElseThrow();
        
        AlteradoPor model = new AlteradoPor();
        model.setId(dto.id());
        model.setAgente(agente);
        model.setTimestamp(DateTimeUtils.getZonedDateTime(dto.timestamp()));
        
        return model;
    }
    
    public AlteradoPorDto toDto(AlteradoPor model){
        if(model == null) return null;
        
        return new AlteradoPorDto(
                model.getId(), 
                model.getAgente().getId(), 
                model.getAgente().getNomeCompleto(), 
                DateTimeUtils.formatZonedDateTime(model.getTimestamp()) 
        );
    }
    
}
