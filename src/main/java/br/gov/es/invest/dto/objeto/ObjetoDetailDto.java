/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.objeto;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.EmStatusDTO;

import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record ObjetoDetailDto(
        Long id,
        String hashProposta,
        String tipoInvestimento,
        String tipoObjeto,
        String nome,
        String descricao,
        String codUnidade,
        String siglaUnidade,
        String responsavel,
        Long microrregiaoId,
        String microrregiaoNome,
        String infoComplementar,
        String codPlano,
        String nomePlano,
        Long idArea,
        String nomeArea,
        List<TipoPlano> tiposPlano,
        String contrato,
        Map<Integer, Map<String, Custo>> custos,
        EmEtapaDTO emEtapa,
        EmStatusDTO emStatus,
        String possuiOrcamento
    ) {
    
    public static record Custo(Double previsto, Double contratado) {}
    
    public static record TipoPlano(Long id, String sigla, String nome){}
}
