package br.gov.es.invest.model;

import java.time.ZonedDateTime;

import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ParecerDTO;
import br.gov.es.invest.utils.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Parecer extends Entidade {
    
    private ZonedDateTime timestamp;
    private String texto;

    @Relationship("EM")
    private Etapa etapa;
        
    @Relationship("FEITO_POR")
    private Agente usuario;

    @Relationship("FEITO_POR")
    private Grupo grupo;

    public static Parecer parse(ParecerDTO dto) {
        if (dto == null) 
            return null;
        
        Parecer parecer = new Parecer();
        parecer.setId(dto.id());
        parecer.setTimestamp(dto.timestamp() == null ? null : DateTimeUtils.getZonedDateTime(dto.timestamp()));
        parecer.setTexto(dto.texto());
        parecer.setEtapa(Etapa.parse(dto.etapa()));
        parecer.setUsuario(Agente.parse(dto.feitoPor()));
        parecer.setGrupo(Grupo.parse(dto.doGrupo()));

        return parecer;

    }

}
