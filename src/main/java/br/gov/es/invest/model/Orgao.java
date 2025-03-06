package br.gov.es.invest.model;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.acessocidadaoapi.OrganizacaoACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Orgao extends Entidade{
    
    private String guid;

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


}
