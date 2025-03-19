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
    private String codPo;
    private String nomePo;
    private String tipoDePlano;
    private String microrregiao;
    private String areaEstrategica;
    private String contrato;
    private String gnd;
    private List<ValoresPorFonte> valoresPorFonte;
}

@Getter
@Setter
@Builder
class ValoresPorFonte {
    private String fonte;
    private List<ValoresPorAno> valoresPorAno;
}

@Getter
@Setter
@Builder
class ValoresPorAno {
    private String ano;
    private String contratado;
    private String previsto;
    
}
