/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.domains;

import br.gov.es.invest.model.AcaoEnum;

/**
 *
 * @author gean.carneiro
 */
public record Acao(
        String acaoId,
        String nome,
        String atividadeFinal,
        Boolean positivo,
        String statusFinal,
        String proxEtapa
) {
    
}
