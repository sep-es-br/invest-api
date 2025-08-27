package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Conta;

public record ContaDto(
    Long id,
    String status,
    String nome,
    PlanoOrcamentarioDTO planoOrcamentario,
    UnidadeOrcamentariaDTO unidadeOrcamentariaImplementadora,
    List<ExecucaoOrcamentariaDto> execucoesOrcamentaria
) {
    public ContaDto(Conta model) {
        this(
            model.getId(), 
            model.getStatus(), 
            model.getNome(), 
            model.getPlanoOrcamentario() == null ? null : new PlanoOrcamentarioDTO(model.getPlanoOrcamentario()), 
            model.getUnidadeOrcamentariaImplementadora() == null ? null : new UnidadeOrcamentariaDTO(model.getUnidadeOrcamentariaImplementadora()), 
            model.getExecucoesOrcamentaria() == null ? null : model.getExecucoesOrcamentaria().stream().map(ExecucaoOrcamentariaDto::parse).toList()
        );
    }


}
