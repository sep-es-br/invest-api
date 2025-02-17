package br.gov.es.invest.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.gov.es.invest.dto.projection.TiraInvestimentoProjection;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvestimentoTiraDTO {

    private String id;
    private String nome;
    private String codPO;
    private String unidadeOrcamentaria;
    private Double totalPrevisto;
    private Double totalContratado;
    private Double totalOrcado;
    private Double totalAutorizado;
    private Double totalEmpenhado;
    private Double totalDisponivel;
    

    private List<ObjetoTiraDTO> objetos;
    
    public InvestimentoTiraDTO(Investimento investimento, List<Objeto> objetos){
        
        this.nome = investimento.getNome();
        this.codPO = investimento.getPlanoOrcamentario().getCodigo();
        UnidadeOrcamentaria unidadeOrcamentaria = investimento.getUnidadeOrcamentariaImplementadora();
        this.unidadeOrcamentaria = unidadeOrcamentaria.getCodigo() + " - " + unidadeOrcamentaria.getSigla();

        this.objetos = objetos.stream().map(objeto -> {
            return new ObjetoTiraDTO(objeto);
        }).collect(Collectors.toList());

        this.totalPrevisto = 0d;
        this.totalContratado = 0d;
        this.totalOrcado = 0d;
        this.totalAutorizado = 0d;
        this.totalDisponivel = 0d;

        this.objetos.forEach(obj -> {
            this.totalPrevisto += obj.getTotalPrevisto();
            this.totalContratado += obj.getTotalOrcado();
            this.totalOrcado += obj.getTotalOrcado();
            this.totalAutorizado += obj.getTotalAutorizado();
            this.totalDisponivel += obj.getTotalDisponivel();
        });

    }

    public static InvestimentoTiraDTO parse(TiraInvestimentoProjection projection, List<Objeto> objetos) {
        if(projection == null) {
            return null;
        }

        InvestimentoTiraDTO investimentoTiraDTO = new InvestimentoTiraDTO();
        investimentoTiraDTO.setId(projection.id());
        investimentoTiraDTO.setCodPO(projection.codPO());
        investimentoTiraDTO.setNome(projection.nome());
        investimentoTiraDTO.setUnidadeOrcamentaria(projection.unidadeOrcamentaria());
        investimentoTiraDTO.setTotalPrevisto(projection.totalPrevisto());
        investimentoTiraDTO.setTotalContratado(projection.totalContratado());
        investimentoTiraDTO.setTotalDisponivel(projection.totalDisponivel());
        investimentoTiraDTO.setTotalEmpenhado(projection.totalEmpenhado());
        investimentoTiraDTO.setTotalOrcado(projection.totalOrcado());
        investimentoTiraDTO.setTotalAutorizado(projection.totalAutorizado());
        
        investimentoTiraDTO.setObjetos(objetos.stream().map(objeto -> {
            return new ObjetoTiraDTO(objeto);
        }).collect(Collectors.toList()));

        return investimentoTiraDTO;
        
    }

    
}