/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.FuncaoDTO;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.UsuarioResponse;
import br.gov.es.invest.dto.acessocidadaoapi.OrganizacaoACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import br.gov.es.invest.model.Avatar;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.service.ACService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class UsuarioFactory {
    
    private final ACService acSrv;
    
    public UsuarioResponse toUsuarioResponse(Agente usuario) {
        
        String token = this.acSrv.getClientToken();
        List<PapelACResponseDto> papeisAc = this.acSrv.getPapeisBySub(usuario.getSub(), token);
        
        String orgao = null;
        PapelACResponseDto papelAC = !papeisAc.isEmpty() ? papeisAc.stream().filter(PapelACResponseDto::Prioritario).findFirst().orElse(papeisAc.get(0)) : null;
        
        if(papelAC != null && papelAC.LotacaoGuid() != null) {
            UnidadeACResponseDto unidade = this.acSrv.getUnidadeInfoByGuid(papelAC.LotacaoGuid(), token);
            if(unidade != null && unidade.guidOrganizacao() != null) {
                OrganizacaoACResponseDto orgaoAc = this.acSrv.getOrgaoInfoByGuid(unidade.guidOrganizacao(), token);
                if(orgaoAc != null) {
                    orgao = orgaoAc.sigla();
                }
            }
        }
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .avatarBlob(Optional.ofNullable(usuario.getImgPerfil()).map(Avatar::getBlob).orElse(null))
                .nome(usuario.getNomeCompleto())
                .email(this.acSrv.getEmailPrincipalBySub(usuario.getSub(), token))
                .orgao(orgao)
                .build();
              
    }
    
    public List<UsuarioResponse> toUsuarioResponse(List<Agente> usuarios) {
        return usuarios.stream().map(this::toUsuarioResponse).collect(Collectors.toList());
    }
    
    public UsuarioDto toUsuarioDto(Agente usuario) {
        return this.toUsuarioDto(usuario, null);
    }
    
    public UsuarioDto toUsuarioDto(Agente usuario, String token) {
        return Optional.ofNullable(usuario)
                .map(_usuario ->  {
                    
                    String tokenAc = this.acSrv.getClientToken();
                    
                    String email = this.acSrv.getEmailPrincipalBySub(_usuario.getSub(), tokenAc);
                    
                    List<PapelACResponseDto> papeisAc = this.acSrv.getPapeisBySub(_usuario.getSub(), tokenAc);
                    
                    return new UsuarioDto(
                        token,
                        _usuario.getId(), 
                        _usuario.getSub(), 
                        AvatarDTO.parse( _usuario.getImgPerfil()), 
                        _usuario.getName(), 
                        _usuario.getNomeCompleto(), 
                        email, 
                        _usuario.getTelefone(),  
                        Optional.ofNullable(_usuario.getRole())
                            .map(roles -> roles.stream()
                                .map(FuncaoDTO::new)
                                .collect(Collectors.toSet())
                            )
                            .orElseGet(Collections::emptySet),
                        papeisAc == null || papeisAc.isEmpty() ? null : papeisAc.stream().map(papel -> this.acSrv.gerarPapelFromRespSemSalvar(papel, tokenAc)).map(PapelDto::parse).collect(Collectors.toSet())
                    );
                })
                .orElse(null);
    }
    
}
