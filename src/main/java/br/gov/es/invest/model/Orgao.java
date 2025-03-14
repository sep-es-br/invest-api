package br.gov.es.invest.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Orgao extends Entidade{
    
    private String guid;

    private String codSigefes;
    private String sigla;
    private String nome;
    
    public Orgao(UnidadesACResponseDto unidadeAC){
        this.guid = unidadeAC.guid();
        this.sigla = unidadeAC.sigla();
        this.nome = unidadeAC.nomeFantasia();
    }

    public Orgao(OrgaoDto dto){
        this.setId(dto.id());
        this.guid = dto.guid();
        this.sigla = dto.sigla();
        this.nome = dto.nome();
    }

    public static Orgao parse(UnidadesACResponseDto unidadeAC) {
        return Optional.ofNullable(unidadeAC).map(Orgao::parse).orElse(null);
    }

    public static Orgao parse(OrgaoDto dto) {
        return Optional.ofNullable(dto).map(Orgao::parse).orElse(null);
    }


}
