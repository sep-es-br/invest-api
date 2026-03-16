package br.gov.es.invest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistroDadoDetalhado {
    private String unidadeResponsavel;
    private String nomeResponsavel;
    private String codPo;
    private String nomePo;
    private String descObjeto;
    private String tipoDePlano;
    private String microrregiao;
    private String areaEstrategica;
    private String contrato;
    private Integer gnd;
    private List<RegistroDadoDetalhadoValoresPorFonte> valoresPorFonte;
}
