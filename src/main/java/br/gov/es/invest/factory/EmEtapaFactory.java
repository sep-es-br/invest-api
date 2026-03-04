/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.EtapaDTO;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DateTimeUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cliente
 */
@Component
public class EmEtapaFactory {
    
    
    
    @Autowired
    private UsuarioService userSrv;
    
    
    public EmEtapaDTO toDto(EmEtapa model) {
        return model == null ? null
        : new EmEtapaDTO(
            model.getId(), 
            EtapaDTO.parse(model.getEtapa()), 
            model.getAtividade(),
            model.isDevolvido(),
            Optional.ofNullable(model.getTimestamp()).map(DateTimeUtils::formatZonedDateTime).orElse(null),
            Optional.ofNullable(model.getAvaliadoEm()).map(DateTimeUtils::formatZonedDateTime).orElse(null),
            Optional.ofNullable(model.getAvaliadoPorId()).flatMap(this.userSrv::findById).map(Agente::getName).orElse(null)
        );
    } 
    
    
}
