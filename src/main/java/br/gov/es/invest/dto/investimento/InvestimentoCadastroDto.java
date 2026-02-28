/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.investimento;

import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import java.util.List;

/**
 *
 * @author gean.carneiro
 */
public record InvestimentoCadastroDto(
            Long id,
            String tipo,
            String nome,
            String descricao,
            String codUnidade,
            String siglaUnidade,
            String codPo,
            String nomePo,
            List<ObjetoCadastroFormDto> objetos
        ) {

}
