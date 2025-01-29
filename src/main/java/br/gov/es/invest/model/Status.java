package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.StatusDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Status extends Entidade {
    
    private String nome;

    public static Status parse (StatusDTO dto) {
        if(dto == null) return null;

        Status status = new Status();
        status.setId(dto.id());
        status.nome = dto.nome();

        return status;
    }
 
}
