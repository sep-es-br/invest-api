/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.utils.domains;

import java.util.List;

/**
 *
 * @author gean.carneiro
 */
public record Etapa(
        String etapaId,
        int ordem,
        String nome,
        String grupoResponsavel,
        List<Acao> acoes
) {
    public Acao acao(String acaoId) {
        List<Acao> result = this.acoes.stream().filter(a -> a.acaoId().equals(acaoId)).toList();
        
        return result.isEmpty() ? null : result.get(0);
    }
}