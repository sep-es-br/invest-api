package br.gov.es.invest.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Investimento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvestimentoDTO {

    private String nome;
    private String codPO;
    private Double totalPrevisto;
    private Double totalHomologado;
    private Double totalOrcado;
    private Double totalAutorizado;
    private Double totalDisponivel;
    

    private List<ObjetoDTO> objetos;
    
    public InvestimentoDTO(Investimento investimento){
        
        this.nome = investimento.getNome();
        this.codPO = investimento.getExecucoesOrcamentariaDelimitadores().get(0).getPlanoOrcamentarioOrientador().getCodigo().toString();

        this.objetos = investimento.getObjetosCusteadores().stream().map(objeto -> {
            return new ObjetoDTO(objeto);
        }).collect(Collectors.toList());

        this.totalPrevisto = 0d;
        this.totalHomologado = 0d;
        this.totalOrcado = 0d;
        this.totalAutorizado = 0d;
        this.totalDisponivel = 0d;

        this.objetos.forEach(obj -> {
            this.totalPrevisto += obj.getTotalPrevisto();
            this.totalHomologado += obj.getTotalOrcado();
            this.totalOrcado += obj.getTotalOrcado();
            this.totalAutorizado += obj.getTotalAutorizado();
            this.totalDisponivel += obj.getTotalDisponivel();
        });

    }

    
}