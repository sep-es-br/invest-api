package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@RelationshipProperties
public class VinculadaPor extends Entidade {

    @TargetNode
    private FonteOrcamentaria fonteOrcamentaria;

    private Double autorizado;
    private Double dispSemReserva;
    private List<Double> empenhado;
    private List<Double> liquidado;
    private List<Double> pago;
    private Double orcado;



}
