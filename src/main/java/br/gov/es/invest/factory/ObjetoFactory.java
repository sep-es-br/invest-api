/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.EmStatusDTO;
import br.gov.es.invest.dto.objeto.ObjetoDetailDto;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.model.Objeto;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class ObjetoFactory {
        
    public ObjetoDetailDto fromModel(Objeto model) {
        return ObjetoDetailDto.builder()
                .tipoInvestimento(model.getConta().getClass().getSimpleName())
                .tipoObjeto(model.getTipo())
                .nome(model.getNome())
                .descricao(model.getDescricao())
                .codUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getCodigo())
                .siglaUnidade(model.getConta().getUnidadeOrcamentariaImplementadora().getSigla())
                .responsavel(model.getResponsavel().getNomeCompleto())
                .microrregiaoId(model.getMicrorregiao().getId())
                .microrregiaoNome(model.getMicrorregiao().getNome())
                .infoComplementar(model.getInfoComplementares())
                .codPlano(model.getConta().getPlanoOrcamentario().getCodigo())
                .nomePlano(model.getConta().getPlanoOrcamentario().getNome())
                .idArea(model.getAreaTematica().getId())
                .nomeArea(model.getAreaTematica().getNome())
                .contrato(model.getContrato())
                .tiposPlano(model.getTiposPlano().stream().map(
                        tpPlano -> new ObjetoDetailDto.TipoPlano(tpPlano.getId(), tpPlano.getNome(), tpPlano.getSigla())
                ).collect(Collectors.toList()))
                .custos(model.getCustosEstimadores().stream()
                    .collect(Collectors.toMap(
                        Custo::getAnoExercicio, 
                        custo -> custo.getIndicadaPor().stream()
                            .collect(Collectors.toMap(
                                    this::getCodFonte,
                                    this::from
                                ))
                        )))
                .emEtapa(EmEtapaDTO.parse(model.getEmEtapa()))
                .emStatus(EmStatusDTO.parse(model.getEmStatus()))
                .hashProposta(model.getHashProposta())
                .possuiOrcamento(model.getPossuiOrcamento())
                .build();
    }
    
    private ObjetoDetailDto.Custo from(IndicadaPor model) {
        return new ObjetoDetailDto.Custo(model.getPrevisto(), model.getContratado());
    }
    
    private String getCodFonte(IndicadaPor model) {
        return model.getFonteOrcamentaria().getCodigo();
    }
    
    private String getNomeFonte(IndicadaPor model) {
        return model.getFonteOrcamentaria().getNome();
    }
    
    
    
}
