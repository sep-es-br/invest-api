/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.investimento;

/**
 *
 * @author gean.carneiro
 */
public record InvestimentoListaDto(
            Long id,
            String codUnidade,
            String siglaUnidade,
            String codPO,
            String nome,
            String tipo,
            Double totalPlanejado,
            Double totalContratado,
            Double totalOrcado,
            Double totalAutorizado,
            Double totalEmpenhado,
            Double totalDisponivel
        ) {

}
