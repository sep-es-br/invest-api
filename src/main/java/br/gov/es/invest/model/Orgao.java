package br.gov.es.invest.model;

import java.util.Optional;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.acessocidadaoapi.OrganizacaoACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Orgao extends NoEntidade{
    
    private String guid;
    private String codigo;
    private String sigla;
    private String nome;
    
    public Orgao(OrganizacaoACResponseDto organizacaoAc){
        this.guid = organizacaoAc.guid();
        this.sigla = organizacaoAc.sigla();
        this.nome = organizacaoAc.nomeFantasia();
    }

    public Orgao(UnidadesACResponseDto organizacaoAc){
        this.guid = organizacaoAc.guid();
        this.sigla = organizacaoAc.sigla();
        this.nome = organizacaoAc.nomeFantasia();
    }

    public Orgao(OrgaoDto dto){
        this.setId(dto.id());
        this.guid = dto.guid();
        this.sigla = dto.sigla();
        this.nome = dto.nome();
    }

    public static Orgao parse(UnidadesACResponseDto unidadeAC) {
        return Optional.ofNullable(unidadeAC).map(Orgao::new).orElse(null);
    }

    public static Orgao parse(OrgaoDto dto) {
        return Optional.ofNullable(dto).map(Orgao::new).orElse(null);
    }


}
