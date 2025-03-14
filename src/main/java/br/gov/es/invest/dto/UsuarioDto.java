package br.gov.es.invest.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Funcao;
import br.gov.es.invest.model.Usuario;

public record UsuarioDto(
        String token,
        String id,
        String sub,
        AvatarDTO imgPerfil,
        String name,
        String nomeCompleto,
        String email,
        String telefone,
        String papel,
        Set<FuncaoDTO> role,

        SetorDto setor
){
        
        public static UsuarioDto parse(Usuario usuario, String token){
                return Optional.ofNullable(usuario)
                        .map(_usuario ->  new UsuarioDto(
                                        token,
                                        _usuario.getId(), 
                                        _usuario.getSub(), 
                                        AvatarDTO.parse( _usuario.getImgPerfil()), 
                                        _usuario.getName(), 
                                        _usuario.getNomeCompleto(), 
                                        _usuario.getEmail(), 
                                        _usuario.getTelefone(), 
                                        _usuario.getPapel(), 
                                        Optional.ofNullable(_usuario.getRole())
                                                .map(roles -> roles.stream()
                                                        .map(FuncaoDTO::new)
                                                        .collect(Collectors.toSet())
                                                )
                                                .orElseGet(Collections::emptySet),
                                        _usuario.getSetor() == null ? null : new SetorDto(_usuario.getSetor())
                        ))
                        .orElse(null);
                
        }

        public static UsuarioDto parse(Usuario usuario){
                return UsuarioDto.parse(usuario, null);
        }
}
