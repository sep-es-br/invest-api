package br.gov.es.invest.dto;

import java.util.Set;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Grupo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrupoDTO {
    private String id;
    private String icone;
    private String sigla;
    private String nome;
    private String descricao;
    private Boolean podeVerTodasUnidades;

    
    private Set<UsuarioDto> membros; 
    private Set<PapelDto> papeisMembro;
    private Set<SetorDto> setoresMembros;
    private Set<OrgaoDto> orgaoMembro;

    private Set<PodeDto> permissoes;


    public GrupoDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.icone = grupo.getIcone();
        this.sigla = grupo.getSigla();
        this.nome = grupo.getNome();
        this.descricao = grupo.getDescricao();
        this.podeVerTodasUnidades = grupo.isPodeVerTodasUnidades();
        
        if(grupo.getMembros() != null)
            this.membros = grupo.getMembros().stream().map(usuario -> new UsuarioDto(usuario)).collect(Collectors.toSet());
        
        if(grupo.getPapeisMembro() != null)
            this.papeisMembro = grupo.getPapeisMembro().stream().map(
                papel -> PapelDto.parse(papel)
            ).collect(Collectors.toSet());

        if(grupo.getSetoresMembro() != null)
            this.setoresMembros = grupo.getSetoresMembro().stream().map(
                setor -> new SetorDto(setor)
            ).collect(Collectors.toSet());

        if(grupo.getOrgaosMembro() != null)
            this.orgaoMembro = grupo.getOrgaosMembro().stream().map(
                orgao -> new OrgaoDto(orgao)
            ).collect(Collectors.toSet());
       
        
        if(grupo.getPermissoes() != null)
            this.permissoes = grupo.getPermissoes().stream().map(permissao -> new PodeDto(permissao)).collect(Collectors.toSet());
    }

    public static GrupoDTO parse (Grupo model) {
        return model == null ? null
        : new GrupoDTO(model);
    }

}
