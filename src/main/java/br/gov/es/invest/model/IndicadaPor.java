package br.gov.es.invest.model;

import java.lang.annotation.Target;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import br.gov.es.invest.dto.IndicadaPorDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@RelationshipProperties
@NoArgsConstructor
public class IndicadaPor extends Entidade {
    
    @TargetNode
    private FonteOrcamentaria fonteOrcamentaria;

    private double previsto;
    private double contratado;

    public IndicadaPor(IndicadaPorDto dto) {
        this.setId(dto.id());
        this.fonteOrcamentaria = new FonteOrcamentaria(dto.fonteOrcamentaria());
        this.previsto = dto.previsto() != null ? dto.previsto() : 0d;
        this.contratado = dto.contratado() != null ? dto.contratado() : 0d;
    }

}
