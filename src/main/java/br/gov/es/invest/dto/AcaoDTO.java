package br.gov.es.invest.dto;

import br.gov.es.invest.model.Acao;

public record AcaoDTO(
    String id,
    String nome, 
    String IdProxEtapa
) {
    public static AcaoDTO parse (Acao model) {
        return model == null ? null :
        new AcaoDTO(
            model.getId(), 
            model.getNome(), 
            model.getIdProximaEtapa()
        );
    }
}
