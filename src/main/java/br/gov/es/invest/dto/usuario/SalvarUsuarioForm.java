/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.usuario;

/**
 *
 * @author gean.carneiro
 */
public record SalvarUsuarioForm(
            String nome,
            String nomeCompleto,
            String email,
            String telefone,
            String sub
        ) {

}
