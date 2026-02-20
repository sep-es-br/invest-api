/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.model;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.util.Assert;

/**
 *
 * @author gean.carneiro
 */
@Node
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ConfigGerais extends Entidade {
    
    @Setter(AccessLevel.NONE)
    private ZonedDateTime inicioRevisaoPip;
    
    @Setter(AccessLevel.NONE)
    private ZonedDateTime fimRevisaoPip;
    
    public void setRevisaoPip(
            ZonedDateTime inicio,
            ZonedDateTime fim
    ) {
        Assert.notNull(inicio, "Data inicial não pode ser nula");
        Assert.notNull(fim, "Data final não pode ser nula");
        Assert.isTrue(fim.isAfter(inicio) || fim.equals(inicio), "não faz sentido a data inicial ser posterior a final");
        
        this.inicioRevisaoPip = inicio;
        this.fimRevisaoPip = fim;
    }
    
    public boolean emPeriodoRevisao(ZonedDateTime referencia) {

        if (inicioRevisaoPip == null || fimRevisaoPip == null) {
            return false;
        }

        return !referencia.isBefore(inicioRevisaoPip)
            && !referencia.isAfter(fimRevisaoPip);
    }
    
}
