/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.grupo;

/**
 *
 * @author Cliente
 */
public record GrupoDoUsuarioListDTO(
            Long idGrupo,
            String sigla,
            String nome,
            String descricao,
            String papel
        ) {

}
