package br.gov.es.invest.dto;

import java.util.Arrays;

import br.gov.es.invest.dto.projection.TiraObjetoProjection;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.VinculadaPor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
public record ObjetoTiraDTO (
    Long id,
    String unidadeResponsavel,
    String codPO,
    String nome,
    String tipo,
    Double totalPlanejado,
    Double totalContratado,
    Double totalOrcado,
    Double totalAutorizado,
    Double totalEmpenhado,
    Double totalDisponivel,
    String status
) {
    
    public static ObjetoTiraDTO parse(Objeto objeto){
        
        if(objeto == null) return null;

        UnidadeOrcamentaria unidadeOrcamentaria = objeto.getConta().getUnidadeOrcamentariaImplementadora();

        double totalPlanejado = 0d;
        double totalHomologado = 0d;


        for(Custo custo : objeto.getCustosEstimadores()){

            for( IndicadaPor indicadaPor : custo.getIndicadaPor() ){
                totalPlanejado += indicadaPor.getPlanejado();
                totalHomologado += indicadaPor.getContratado();
            }

        }
        
        double totalOrcado = 0d;
        double totalAutorizado = 0d;
        double totalDisponivel = 0d;
        double totalEmpenhado = 0d;

        for ( ExecucaoOrcamentaria exec : objeto.getConta().getExecucoesOrcamentaria() ) {

            for ( VinculadaPor vinculadaPor : exec.getVinculadaPor() ) {
                totalOrcado += vinculadaPor.getOrcado();
                totalAutorizado += vinculadaPor.getAutorizado();
                totalDisponivel += vinculadaPor.getDispSemReserva();
                totalEmpenhado += Arrays.stream(vinculadaPor.getEmpenhado()).reduce(0, Double::sum); 
            }

        }


        return ObjetoTiraDTO.builder()
                .id(objeto.getId())
                .unidadeResponsavel(unidadeOrcamentaria.getCodigo() + " - " + unidadeOrcamentaria.getSigla())
                .codPO(objeto.getConta().getPlanoOrcamentario() == null ? "Sem PO" : objeto.getConta().getPlanoOrcamentario().getCodigo())
                .nome(objeto.getNome())
                .tipo(objeto.getTipo())
                .totalPlanejado(totalPlanejado)
                .totalContratado(totalHomologado)
                .totalOrcado(totalOrcado)
                .totalAutorizado(totalAutorizado)
                .totalEmpenhado(totalEmpenhado)
                .totalDisponivel(totalDisponivel)
                .status(objeto.getEmStatus() == null ? "null" : objeto.getEmStatus().getStatus().getNome())
                .build();
        
    }

    public static ObjetoTiraDTO parse(TiraObjetoProjection projection) {
        
        return projection == null ? null
        : ObjetoTiraDTO.builder()
                        .id(projection.id())
                        .unidadeResponsavel(projection.unidadeOrcamentaria())
                        .codPO(projection.codPo())
                        .nome(projection.nome())
                        .tipo(projection.tipo())
                        .totalPlanejado(projection.totalPlanejado())
                        .totalContratado(projection.totalContratado())
                        .totalOrcado(projection.totalOrcado())
                        .totalAutorizado(projection.totalAutorizado())
                        .totalEmpenhado(projection.totalEmpenhado())
                        .totalDisponivel(projection.totalDisponivel())
                        .status(projection.status())
                        .build();


    }
}
