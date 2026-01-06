/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.objeto;

/**
 *
 * @author gean.carneiro
 */
public record ObjetoTiraSimplesDto(
        Long id,
        String nome,
        Double previsto,
        Double contratado
    
    ) {

}
