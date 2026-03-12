/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.RevisorDto;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.RevisadoPor;
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
public class RevisadoPorFactory {
    
    private final UsuarioService userSrv;
    
    public RevisadoPor toModel(RevisorDto dto) {
        if(dto == null) return null;
        
        Agente agente = this.userSrv.findById(dto.idRevisor()).orElseThrow();
        
        RevisadoPor model = new RevisadoPor();
        model.setId(dto.id());
        model.setRevisor(agente);
        model.setTimestamp(DateTimeUtils.getZonedDateTime(dto.timestamp()));
        
        return model;
    }
    
    public RevisorDto toDto(RevisadoPor model){
        if(model == null) return null;
        
        return new RevisorDto(
                model.getId(), 
                model.getRevisor().getId(), 
                model.getRevisor().getNomeCompleto(), 
                DateTimeUtils.formatZonedDateTime(model.getTimestamp()) 
        );
    }
    
}
