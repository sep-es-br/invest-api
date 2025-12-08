/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.FonteOrcamentariaDTO;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.service.FonteOrcamentariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class FonteFactory {
    
    private final FonteOrcamentariaService fonteOrcamentariaSrv;
    
    public FonteOrcamentaria fromDto(FonteOrcamentariaDTO dto) {
        
        if(dto.getCodigo() == null || dto.getCodigo().isBlank()) throw new IllegalArgumentException("Fonte Orçamentária " + dto.getNome() + " está sem código!!");
        
        FonteOrcamentaria fonte = new FonteOrcamentaria(dto);
        
        this.fonteOrcamentariaSrv.findByCodigo(dto.getCodigo()).map(FonteOrcamentaria::getId).ifPresent(fonte::setId);
        
        return fonte;
        
    }
    
}
