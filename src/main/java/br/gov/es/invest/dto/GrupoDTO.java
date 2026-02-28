package br.gov.es.invest.dto;

import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Grupo;

public record GrupoDTO(
    Long id,
    String icone,
    String sigla,
    String nome,
    String descricao,
    Set<UsuarioDto> membros,
    Set<PodeDto> permissoes,
    Set<PapelDto> papeisMembro,
    Set<SetorDto> setoresMembros,
    Set<OrgaoDto> orgaoMembro
    ){


    public GrupoDTO(Grupo grupo) {
        this(
            grupo.getId(), 
            grupo.getIcone(), 
            grupo.getSigla(), 
            grupo.getNome(), 
            grupo.getDescricao(), 
            (grupo.getMembros() != null) ? grupo.getMembros().stream().map(UsuarioDto::parse).collect(Collectors.toSet()) : null, 
            (grupo.getPermissoes() != null) ? grupo.getPermissoes().stream().map(permissao -> new PodeDto(permissao)).collect(Collectors.toSet()) : null, 
            (grupo.getPapeisMembro() != null) ? grupo.getPapeisMembro().stream().map(
                papel -> PapelDto.parse(papel)
            ).collect(Collectors.toSet()) : null, 
            (grupo.getSetoresMembro() != null) ? grupo.getSetoresMembro().stream().map(
                setor -> new SetorDto(setor)
            ).collect(Collectors.toSet()) : null, 
            (grupo.getOrgaosMembro() != null) ? grupo.getOrgaosMembro().stream().map(
                orgao -> new OrgaoDto(orgao)
            ).collect(Collectors.toSet()) : null
        );
               
        
    }

    
    public static GrupoDTO parse (Grupo model) {
        return model == null ? null
        : new GrupoDTO(model);
    }


}

