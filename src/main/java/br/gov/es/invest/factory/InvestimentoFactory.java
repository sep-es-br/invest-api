/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.investimento.InvestimentoDetailDto;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.service.ObjetoService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class InvestimentoFactory {
    
    @Autowired
    private ObjetoService objSrv;
    
    @Autowired
    private ObjetoFactory objFactory;
    
    
    public InvestimentoDetailDto toInvestimentoDetalDto(Investimento model){
        if(model == null) return null;
        
        return InvestimentoDetailDto.builder()
                .id(model.getId())
                .tipo(model.getTipoConta().toString())
                .nome(Optional.ofNullable(model.getNome()).orElse(model.getPlanoOrcamentario().getNome()))
                .descricao(model.getDescricao())
                .codUnidade(model.getUnidadeOrcamentariaImplementadora().getCodigo())
                .codPO(model.getPlanoOrcamentario().getCodigo())
                .objetos(this.objSrv.findObjetoByConta(model.getId()).stream().map(Objeto::getId).map(this.objFactory::gerarTiraSimples).collect(Collectors.toList()))
                .build();
    }
}
