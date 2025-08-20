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

public record Fluxo(
    String fluxoId,
    String nome,
    String etapaInicial,
    List<Etapa> etapas
) {
    public Etapa etapa(String etapaId){ 
        List<Etapa> result = this.etapas.stream().filter(e -> e.etapaId().equals(etapaId)).toList();
        
        return result.isEmpty() ? null : result.get(0);
    }
    
    public Acao acao(String acaoId) {
        for(Etapa etapa : etapas){
            Acao result = etapa.acao(acaoId);
            if(result != null) return result;
        }
        
        return null;
    }
}
