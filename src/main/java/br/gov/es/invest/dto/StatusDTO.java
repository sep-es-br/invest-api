package br.gov.es.invest.dto;

import br.gov.es.invest.model.Status;

public record StatusDTO(
    String id,
    String nome
) {
    public static StatusDTO parse(Status model) {
        return model == null ? null :
            new StatusDTO(model.getId(), model.getNome());

    }
}
