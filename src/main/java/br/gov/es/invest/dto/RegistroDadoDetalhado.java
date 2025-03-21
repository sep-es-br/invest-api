package br.gov.es.invest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistroDadoDetalhado {
    private String unidadeResponsável;
    private String emailResponsavel;
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
