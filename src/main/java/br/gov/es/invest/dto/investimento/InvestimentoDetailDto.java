/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.investimento;

import br.gov.es.invest.dto.objeto.ObjetoDetailDto;
import java.util.List;
import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record InvestimentoDetailDto(
        Long id,
        String tipo,
        String nome,
        String descricao,
        String codUnidade,
        String codPO,
        List<ObjetoDetailDto> objetos
    ) {
    
    
}
