package br.gov.es.invest.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private ArrayList<Double> empenhado = new ArrayList<>(12);
    private ArrayList<Double> liquidado = new ArrayList<>(12);
    private ArrayList<Double> pago = new ArrayList<>(12);
    private Double orcado;



}
