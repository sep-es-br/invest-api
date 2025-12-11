/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto;

import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record UsuarioResponse(
        
        Long id,
        String avatarBlob,
        String nome,
        String email,
        String orgao
        
    ) {

}
