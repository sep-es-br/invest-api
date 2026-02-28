/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.dto.objeto;

import br.gov.es.invest.dto.FonteOrcamentariaDTO;
import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.dto.TipoPlanoDto;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import java.util.List;
import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record ObjetoCadastroFormDto(
            Long id,
            Integer gnd,
            String tipoConta,
            String tipo,
            String hashProposta,
            String nome,
            String descricao,
            Long microregiaoId,
            String infoComplementares,
            List<TipoPlanoDto> planos,
            String contrato,
            Long areaTematicaId,
            List<Custo> recursos,
            PlanoOrcamentarioDTO planoOrcamentario,
            UnidadeOrcamentariaDTO unidadeOrcamentaria,
            String possuiOrcamento
        ) {
    public static record Custo(Integer ano, List<ValoresFonte> valoresFontes){}
    
    public static record ValoresFonte(FonteOrcamentariaDTO fonte, Double planejado, Double contratado){}
}


