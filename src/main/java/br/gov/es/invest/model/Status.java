package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.StatusDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Status extends Entidade {
    
    private String nome;
    private StatusEnum statusId;

    public static Status parse (StatusDTO dto) {
        if(dto == null) return null;

        Status status = new Status();
        status.setId(dto.id());
        status.nome = dto.nome();
        status.statusId = StatusEnum.valueOf(dto.statusId());

        return status;
    }
 
}
