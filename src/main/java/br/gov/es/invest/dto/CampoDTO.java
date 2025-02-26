package br.gov.es.invest.dto;

import br.gov.es.invest.model.Campo;

public record CampoDTO(
    String id,
    String campoId,
    String nome
) {
    public static CampoDTO parse(Campo model) {
        return model == null ? null
        : new CampoDTO(
            model.getId(), 
            model.getCampoId(), 
            model.getNome()
        );
    }
}
