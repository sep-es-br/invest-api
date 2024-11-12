package br.gov.es.invest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObjetoFiltroDTO {
    private String exercicio;
    private String nome;
    private String unidadeId;
    private String status;
}
