package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Node
public class Conta extends Entidade implements Serializable {
    private String status;
    private String nome;

  
    @Relationship(type = "ORIENTA", direction = Direction.INCOMING)
    private PlanoOrcamentario planoOrcamentario;

    @Relationship(type = "IMPLEMENTA", direction = Direction.INCOMING)
    private UnidadeOrcamentaria unidadeOrcamentariaImplementadora;
    
    @Relationship(type = "DELIMITA", direction = Direction.INCOMING)
    private List<ExecucaoOrcamentaria> execucoesOrcamentaria;

    public void filtrarExecucoes(Integer anoExecucao, String fonteId) {
        if(anoExecucao != null){
            this.setExecucoesOrcamentaria(
                this.getExecucoesOrcamentaria().stream()
                .filter(exec -> exec.getAnoExercicio().equals(anoExecucao))
                .toList()
            );
        }

        if(fonteId != null) {
            for(ExecucaoOrcamentaria exec : this.getExecucoesOrcamentaria()){
                exec.setVinculadaPor(new HashSet<>(
                    exec.getVinculadaPor().stream()
                    .filter(vp -> vp.getFonteOrcamentaria().getId().equals(fonteId))
                    .collect(Collectors.toSet())
                ));
            }
        }
    }

}
