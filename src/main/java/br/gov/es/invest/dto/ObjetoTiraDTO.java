package br.gov.es.invest.dto;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObjetoTiraDTO {
    
    private String unidadeResponsavel;
    private String codPlano;
    private String nome;
    private String tipo;
    private Double totalPrevisto;
    private Double totalHomologado;
    private Double totalOrcado;
    private Double totalAutorizado;
    private Double totalDisponivel;
    private String status;

    public ObjetoTiraDTO(Objeto objeto, Conta conta) {

        this.nome = objeto.getNome();
        this.tipo = objeto.getTipo();

        UnidadeOrcamentaria unidadeOrcamentaria = conta.getUnidadeOrcamentariaImplementadora();
        this.unidadeResponsavel = unidadeOrcamentaria.getCodigo() + " - " + unidadeOrcamentaria.getSigla();
        this.codPlano = conta.getPlanoOrcamentarioOrientador().getCodigo();

        this.totalPrevisto = 0d;
        this.totalHomologado = 0d;
        this.totalOrcado = 0d;
        this.totalAutorizado = 0d;
        this.totalDisponivel = 0d;
        this.status = objeto.getStatus();

        objeto.getCustosEstimadores().forEach(custo -> {

            custo.getIndicadaPor().forEach(indicadaPor -> {

                this.totalPrevisto += indicadaPor.getPrevisto();
                this.totalHomologado += indicadaPor.getContratado();
            });

        });

        conta.getExecucoesOrcamentaria().forEach(exec -> {

            exec.getVinculadaPor().forEach(vinculadaPor -> {
                this.totalOrcado += vinculadaPor.getOrcado();
                this.totalAutorizado += vinculadaPor.getAutorizado();
                this.totalDisponivel += vinculadaPor.getDispSemReserva();
            });

            
        });
    }
}
