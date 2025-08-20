package br.gov.es.invest.model;

import java.time.ZonedDateTime;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ParecerDTO;
import br.gov.es.invest.service.EtapaService;
import br.gov.es.invest.utils.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Parecer extends Entidade {
    
    private ZonedDateTime timestamp;
    private String texto;

    @Transient
    private String etapaId;
    
    @Relationship("EM")
    private Etapa etapa;
        
    @Relationship("FEITO_POR")
    private Usuario usuario;

    @Relationship("FEITO_POR")
    private Grupo grupo;
    
    public Parecer hidratar(EtapaService etapaSrv) {
        if(etapaId != null) {
            this.etapa = etapaSrv.getByEtapaId(etapaId).orElseThrow();
        }
        
        return this;
    }
    
    public static Parecer parse(ParecerDTO dto) {
        if (dto == null) 
            return null;
        
        Parecer parecer = new Parecer();
        parecer.setId(dto.id());
        parecer.setTimestamp(dto.timestamp() == null ? null : DateTimeUtils.getZonedDateTime(dto.timestamp()));
        parecer.setTexto(dto.texto());
        parecer.setEtapaId(dto.etapa());
        parecer.setUsuario(Usuario.parse(dto.feitoPor()));
        parecer.setGrupo(Grupo.parse(dto.doGrupo()));

        return parecer;

    }

}
