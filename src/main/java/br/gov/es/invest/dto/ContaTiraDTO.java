package br.gov.es.invest.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContaTiraDTO {

    private String nome;
    private String codPO;
    private String unidadeOrcamentaria;
    private Double totalPrevisto;
    private Double totalHomologado;
    private Double totalOrcado;
    private Double totalAutorizado;
    private Double totalDisponivel;
    

    private List<ObjetoTiraDTO> objetos;
    
    public ContaTiraDTO(Conta conta, List<Objeto> objetos){
        
        this.nome = conta.getNome();
        this.codPO = conta.getPlanoOrcamentario().getCodigo();
        UnidadeOrcamentaria unidadeOrcamentaria = conta.getUnidadeOrcamentariaImplementadora();
        this.unidadeOrcamentaria = unidadeOrcamentaria.getCodigo() + " - " + unidadeOrcamentaria.getSigla();

        this.objetos = objetos.stream().map(objeto -> {
            return new ObjetoTiraDTO(objeto);
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