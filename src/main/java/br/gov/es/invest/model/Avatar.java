package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.AvatarDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Avatar extends Entidade{
    
    private String blob;

    public Avatar(String blob) {
        this.blob = blob;
    }

    public Avatar(AvatarDTO dto) {
        this.setId(dto.getId());
        this.blob = dto.getBlob();
    }
}
