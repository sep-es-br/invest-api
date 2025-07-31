package br.gov.es.invest.dto;

import br.gov.es.invest.model.Acao;

public record AcaoDTO(
    Long id,
    String nome,
    String acaoId,
    StatusDTO statusFinal,
    String atividadeFinal,
    Boolean positivo,
    Long proxEtapaId
) {
    public static AcaoDTO parse (Acao model) {
        return model == null ? null :
        new AcaoDTO(
            model.getId(), 
            model.getNome(),
            model.getAcaoId().name(),
            StatusDTO.parse(model.getStatusFinal()) ,
            model.getAtividadeFinal(),
            model.getPositivo(),
            model.getProxEtapa() == null ? null : model.getProxEtapa().getId()
        );
    }
}
