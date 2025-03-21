package br.gov.es.invest.model;

import java.lang.annotation.Target;
import java.util.Optional;

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
    private int gnd;

    public IndicadaPor(IndicadaPorDto dto) {
        this.setId(dto.id());
        this.fonteOrcamentaria = new FonteOrcamentaria(dto.fonteOrcamentaria());
        this.previsto = Optional.ofNullable(dto.previsto()).orElse(0d);
        this.contratado = Optional.ofNullable(dto.contratado()).orElse(0d);
        this.gnd = dto.gnd();
    }

    public static IndicadaPor parse(IndicadaPorDto dto) {

        return Optional.ofNullable(dto).map(IndicadaPor::new).orElse(null);

    }

}
