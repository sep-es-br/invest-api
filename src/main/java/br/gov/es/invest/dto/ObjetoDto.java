package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;

public record ObjetoDto(
    String id,
    String tipoConta,
    String tipo,
    String nome,
    String descricao,
    LocalidadeDto microregiaoAtendida,
    String infoComplementares,
    List<TipoPlanoDto> planos,
    String contrato,
    AreaTematicaDto areaTematica,
    List<CustoDTO> recursosFinanceiros,
    UsuarioDto responsavel,
    ContaDto conta
) {
    
    public ObjetoDto(Objeto model) {
        this(
            model.getId(), 
            "Investimento", 
            model.getTipo(), 
            model.getNome(), 
            model.getDescricao(), 
            model.getMicrorregiao() == null ? null : new LocalidadeDto(model.getMicrorregiao()), 
            model.getInfoComplementares(), 
            model.getTiposPlano() == null ? null : model.getTiposPlano().stream().map(tipo -> new TipoPlanoDto(tipo)).toList(),
            model.getContrato(),
            model.getAreaTematica() == null ? null : new AreaTematicaDto(model.getAreaTematica()),
            model.getCustosEstimadores().stream().map(custo -> new CustoDTO(custo)).sorted((c1, c2) -> c1.getAnoExercicio().compareTo(c2.getAnoExercicio())).toList(),
            model.getResponsavel() == null ? null : new UsuarioDto(model.getResponsavel()),
            new ContaDto(model.getConta())
        );
    }


}
