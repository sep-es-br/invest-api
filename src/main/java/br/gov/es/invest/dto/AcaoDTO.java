package br.gov.es.invest.dto;

import br.gov.es.invest.utils.domains.Acao;

public record AcaoDTO(
    String nome,
    String acaoId,
    String statusFinal,
    String atividadeFinal,
    Boolean positivo,
    String proxEtapaId
) {
    public static AcaoDTO parse (Acao model) {
        return model == null ? null :
        new AcaoDTO(
            model.nome(),
            model.acaoId(),
            model.statusFinal() ,
            model.atividadeFinal(),
            model.positivo(),
            model.proxEtapa()
        );
    }
}
