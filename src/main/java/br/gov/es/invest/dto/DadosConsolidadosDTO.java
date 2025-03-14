package br.gov.es.invest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DadosConsolidadosDTO {
    private String idUnidade;
    private String unidadeResponsavel;
    private String idPO;
    private String codPO;
    private String nomePO;
    private Boolean projEstrategico;
    private String contrato;
    private Integer anoExercicio;
    private String custoId;
    private List<DadosConsolidadosValores> valores;
}