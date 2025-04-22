package br.gov.es.invest.dto;

import java.util.Optional;

import br.gov.es.invest.model.Avatar;

public record AvatarDTO(
    String id,
    String blob
) {

    public AvatarDTO(Avatar avatar){
        this(
            avatar.getId(),
            avatar.getBlob()
        );
    }

    public static AvatarDTO parse(Avatar avatar) {
        return Optional.ofNullable(avatar).map(AvatarDTO::new).orElse(null);
    }


}
