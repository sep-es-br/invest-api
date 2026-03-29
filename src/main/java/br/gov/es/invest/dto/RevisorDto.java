/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto;

/**
 *
 * @author Cliente
 */
public record RevisorDto(
        Long id,
        Long idRevisor,
        String nomeRevisor,
        String timestamp
    ) {

}
