package br.gov.es.invest.dto;

import br.gov.es.invest.model.Conta;
import java.util.List;

public record ContaDto(
    Long id,
    String tipoConta,
    String status,
    String nome,
    PlanoOrcamentarioDTO planoOrcamentario,
    UnidadeOrcamentariaDTO unidadeOrcamentariaImplementadora,
    List<ExecucaoOrcamentariaDto> execucoesOrcamentaria
) {
    public ContaDto(Conta model) {
        this(
            model.getId(), 
            model.getTipoConta().toString(),
            model.getStatus(), 
            model.getNome(), 
            model.getPlanoOrcamentario() == null ? null : new PlanoOrcamentarioDTO(model.getPlanoOrcamentario()), 
            model.getUnidadeOrcamentariaImplementadora() == null ? null : new UnidadeOrcamentariaDTO(model.getUnidadeOrcamentariaImplementadora()), 
            model.getExecucoesOrcamentaria() == null ? null : model.getExecucoesOrcamentaria().stream().map(ExecucaoOrcamentariaDto::parse).toList()
        );
    }


}
