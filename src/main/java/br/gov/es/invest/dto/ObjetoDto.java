package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;

public record ObjetoDto(
    String id,
    String tipoConta,
    String tipo,
    String nome,
    EmStatusDTO emStatus,
    EmEtapaDTO emEtapa, 
    String descricao,
    LocalidadeDto microregiaoAtendida,
    String infoComplementares,
    List<TipoPlanoDto> planos,
    String contrato,
    AreaTematicaDto areaTematica,
    List<CustoDTO> recursosFinanceiros,
    UsuarioDto responsavel,
    ContaDto conta,
    List<ApontamentoDTO> apontamentos
) {
    
    public ObjetoDto(Objeto model) {
        this(
            model.getId(), 
            "Investimento", 
            model.getTipo(), 
            model.getNome(), 
            EmStatusDTO.parse(model.getEmStatus()),
            EmEtapaDTO.parse(model.getEmEtapa()),
            model.getDescricao(), 
            model.getMicrorregiao() == null ? null : new LocalidadeDto(model.getMicrorregiao()), 
            model.getInfoComplementares(), 
            model.getTiposPlano() == null ? null : model.getTiposPlano().stream().map(tipo -> new TipoPlanoDto(tipo)).toList(),
            model.getContrato(),
            model.getAreaTematica() == null ? null : new AreaTematicaDto(model.getAreaTematica()),
            model.getCustosEstimadores().stream().map(custo -> new CustoDTO(custo)).sorted((c1, c2) -> c1.getAnoExercicio().compareTo(c2.getAnoExercicio())).toList(),
            model.getResponsavel() == null ? null : new UsuarioDto(model.getResponsavel()),
            new ContaDto(model.getConta()),
            model.getApontamentos().stream().map(ApontamentoDTO::parse).toList()
        );
    }


}
