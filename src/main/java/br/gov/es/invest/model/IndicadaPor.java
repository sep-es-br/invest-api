package br.gov.es.invest.model;

import java.lang.annotation.Target;
import java.util.Optional;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import br.gov.es.invest.dto.IndicadaPorDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@RelationshipProperties
@NoArgsConstructor
@SuperBuilder
public class IndicadaPor extends RelacionamentoEntidade {
    
    @TargetNode
    private FonteOrcamentaria fonteOrcamentaria;

    private double planejado;
    private double contratado;

    public IndicadaPor(IndicadaPorDto dto) {
        this.setId(dto.id());
        this.fonteOrcamentaria = new FonteOrcamentaria(dto.fonteOrcamentaria());
        this.planejado = Optional.ofNullable(dto.planejado()).orElse(0d);
        this.contratado = Optional.ofNullable(dto.contratado()).orElse(0d);
    }

    public static IndicadaPor parse(IndicadaPorDto dto) {

        return Optional.ofNullable(dto).map(IndicadaPor::new).orElse(null);

    }

}
