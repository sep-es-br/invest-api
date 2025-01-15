package br.gov.es.invest.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Conta;


public record ContaDto(
    String id,
    String status,
    String nome,
    PlanoOrcamentarioDTO planoOrcamentario,
    UnidadeOrcamentariaDTO unidadeOrcamentariaImplementadora,
    HashSet<ObjetoDto> objetos,
    List<ExecucaoOrcamentariaDto> execucoesOrcamentaria
) {
    public ContaDto(Conta model) {
        this(
            model.getId(), 
            model.getStatus(), 
            model.getNome(), 
            model.getPlanoOrcamentarioOrientador() == null ? null : new PlanoOrcamentarioDTO(model.getPlanoOrcamentarioOrientador()), 
            model.getUnidadeOrcamentariaImplementadora() == null ? null : new UnidadeOrcamentariaDTO(model.getUnidadeOrcamentariaImplementadora()), 
            new HashSet<>(), 
            model.getExecucoesOrcamentaria() == null ? null : model.getExecucoesOrcamentaria().stream().map(ExecucaoOrcamentariaDto::parse).toList()
        );
    }

    public static ContaDto parse(Conta model){
        ContaDto contaDto = new ContaDto(model);

        Set<ObjetoDto> objDtoList = model.getObjetos().stream()
                .map(obj -> new ObjetoDto(obj, contaDto))
                .collect(Collectors.toSet());

        contaDto.objetos.addAll(objDtoList);

        return contaDto;
    }

}
