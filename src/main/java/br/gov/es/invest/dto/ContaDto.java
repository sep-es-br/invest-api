package br.gov.es.invest.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Objeto;


public record ContaDto(
    String id,
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
