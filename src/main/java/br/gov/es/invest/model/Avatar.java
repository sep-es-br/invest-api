package br.gov.es.invest.model;

import java.util.Optional;

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
        this.setId(dto.id());
        this.blob = dto.blob();
    }

    public static Avatar parse(AvatarDTO dto) {
        return Optional.ofNullable(dto).map(Avatar::parse).orElse(null);
    }
}
