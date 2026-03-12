/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.model;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 *
 * @author Cliente
 */
@Getter
@Setter
@NoArgsConstructor
@RelationshipProperties
public class RevisadoPor extends RelacionamentoEntidade {
    
    @TargetNode
    private Agente revisor;
    
    private ZonedDateTime timestamp;
    
}
