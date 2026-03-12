package br.gov.es.invest.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@RelationshipProperties
@SuperBuilder
public class VinculadaPor extends RelacionamentoEntidade {

    @TargetNode
    private FonteOrcamentaria fonteOrcamentaria;

    private double autorizado;
    private double dispSemReserva;
    @Builder.Default
    private double[] empenhado = new double[12];
    @Builder.Default
    private double[] liquidado = new double[12];
    @Builder.Default
    private double[] pago = new double[12];
    private double orcado;
    private int gnd;
    private boolean novo = false;

    



}
