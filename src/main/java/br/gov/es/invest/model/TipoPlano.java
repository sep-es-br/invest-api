package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.TipoPlanoDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@Node
@SuperBuilder
public class TipoPlano extends NoEntidade {
    
    private String nome;
    private String sigla;

    public TipoPlano(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public TipoPlano(TipoPlanoDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
        this.sigla = dto.sigla();
    }

}
