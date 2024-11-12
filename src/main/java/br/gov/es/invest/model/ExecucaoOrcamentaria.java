package br.gov.es.invest.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class ExecucaoOrcamentaria extends Entidade implements Serializable {
    
    private String anoExercicio;
    private double orcamento;
    private double autorizado;
    private double[] liquidado;

    @Relationship(type = "DELIMITA", direction = Direction.OUTGOING)
    private Conta contaDelimitada;

    @Relationship(type = "VINCULA", direction = Direction.INCOMING)
    private FonteOrcamentaria fonteOrcamentariaVinculadora;

    @Relationship(type = "ORIENTA", direction = Direction.INCOMING)
    private PlanoOrcamentario planoOrcamentarioOrientador;

    @Relationship(type = "IMPLEMENTA", direction = Direction.INCOMING)
    private UnidadeOrcamentaria unidadeOrcamentariaImplementadora;

    public ExecucaoOrcamentaria(String ano, double orcamento, double autorizado, double[] liquidado, Conta contaDelimitada) {
        this.anoExercicio = ano;
        this.orcamento = orcamento;
        this.autorizado = autorizado;
        this.liquidado = liquidado == null ? new double[12] : liquidado;
        this.setContaDelimitada(contaDelimitada);
    }

    public ExecucaoOrcamentaria(String ano, PlanoOrcamentario po, UnidadeOrcamentaria uo, Conta conta) {
        this.anoExercicio = ano;
        this.orcamento = 0d;
        this.autorizado = 0d;
        this.liquidado = new double[12];
        this.contaDelimitada = conta;
        this.planoOrcamentarioOrientador = po;
        this.unidadeOrcamentariaImplementadora = uo;
    }

    public void setValores(double orcamento, double autorizado, double[] liquidado, FonteOrcamentaria fonte){
        this.orcamento = orcamento;
        this.autorizado = autorizado;
        this.liquidado = liquidado == null ? new double[12] : liquidado;
        this.fonteOrcamentariaVinculadora = fonte;
    }

    public static List<ExecucaoOrcamentaria> gerarExecucoes(PlanoOrcamentario po, Conta conta){
        ArrayList<ExecucaoOrcamentaria> execucoes = new ArrayList<>();
        String ano = String.valueOf(LocalDate.now().getYear()+1);

        for (UnidadeOrcamentaria uo : po.getUnidadesOrcamentariaControladoras()) {
            execucoes.add(criar(ano, po, uo, conta));
        }
        
        return execucoes;
    }

    public static ExecucaoOrcamentaria criar(String ano, PlanoOrcamentario po, UnidadeOrcamentaria uo, Conta conta){
        ExecucaoOrcamentaria novo = new ExecucaoOrcamentaria(ano, po, uo, conta);
        DataMock.noExecucaoOrcamentarias.add(novo);
        return novo;
    }

    public static ExecucaoOrcamentaria findOrCreate(String ano, PlanoOrcamentario po, UnidadeOrcamentaria uo, Conta conta){
        List<ExecucaoOrcamentaria> result = DataMock.noExecucaoOrcamentarias.stream()
            .filter(exec -> {
                return exec.anoExercicio.equals(ano)
                    && exec.planoOrcamentarioOrientador.equals(po)
                    && exec.unidadeOrcamentariaImplementadora.equals(uo)
                    && exec.contaDelimitada.equals(conta);
            }).toList();
        
        if(result.isEmpty()) {
            ExecucaoOrcamentaria nova = criar(ano, po, uo, conta);
            ((ArrayList<ExecucaoOrcamentaria>) conta.getExecucoesOrcamentariaDelimitadores()).add(nova);
            return nova;
        } else {
            return result.get(0);
        }
    }

}
