package br.gov.es.invest.dto;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Grupo;

public record GrupoDTO(
    String id,
    String icone,
    String sigla,
    String nome,
    String descricao,
    Boolean podeVerTodasUnidades,

    Set<UsuarioDto> membros,
    Set<PodeDto> permissoes
){
    public static GrupoDTO parse (Grupo model) {
        return model == null ? null
        : new GrupoDTO(
            model.getId(), 
            model.getIcone(), 
            model.getSigla(), 
            model.getNome(), 
            model.getDescricao(), 
            model.isPodeVerTodasUnidades(), 
            
            model.getMembros() == null ? null : model.getMembros().stream().map(UsuarioDto::parse).collect(Collectors.toSet()), 
            model.getPermissoes() == null ? null : model.getPermissoes().stream().map(permissao -> new PodeDto(permissao)).collect(Collectors.toSet())
        );
    }


}

