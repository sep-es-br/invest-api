package br.gov.es.invest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosDetalhadoDTO {
    private Long idUnidade;
    private String unidadeResponsavel;
    private Long idPO;
    private String codPO;
    private String nomePO;
    private Boolean projEstrategico;
    private String contrato;
    private Integer anoExercicio;
    private List<DadosDetalhadoValores> valores;
}