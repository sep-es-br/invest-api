package br.gov.es.invest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvestimentoFiltroDTO {
    private String nome;
    private String codUnidade;
    private String codPO;
    private String idFonte;
    private String exercicio;
}
