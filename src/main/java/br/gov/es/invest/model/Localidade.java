package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.LocalidadeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Localidade extends Entidade {
    
    private String nome;

    public Localidade(LocalidadeDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
    }

    public Localidade(String nome) {
        this.nome = nome;
    }

}
