package br.gov.es.invest.dto;

import br.gov.es.invest.model.Avatar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvatarDTO {
    private String id;
    private String blob;

    public AvatarDTO(Avatar avatar){
        this.id = avatar.getId();
        this.blob = avatar.getBlob();
    }
}
