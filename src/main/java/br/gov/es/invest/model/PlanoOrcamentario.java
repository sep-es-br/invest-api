package br.gov.es.invest.model;

import java.io.Serializable;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class PlanoOrcamentario extends Entidade implements Serializable{
    
    private String codigo;
    private String nome;
    private String descricao;

    public PlanoOrcamentario(PlanoOrcamentarioDTO dto){
        this.setId(dto.id());
        this.codigo = dto.codigo();
        this.nome = dto.nome();
    }

}
