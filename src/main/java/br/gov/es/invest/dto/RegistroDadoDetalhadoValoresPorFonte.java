package br.gov.es.invest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RegistroDadoDetalhadoValoresPorFonte {
    private String fonte;
    private List<RegistroDadoDetalhadoValoresPorAno> valoresPorAno;
}
