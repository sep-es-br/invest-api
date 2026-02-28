package br.gov.es.invest.model;

import java.time.ZonedDateTime;

import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ApontamentoDTO;
import br.gov.es.invest.utils.DateTimeUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Apontamento extends Entidade{
    
    @CreatedDate
    private ZonedDateTime timestamp;
    
    private String texto;
    private boolean active;

    @Relationship("EM")
    private Etapa etapa;
    
    @Relationship("SOBRE")
    private Campo campo;
    
    @Relationship("FEITO_POR")
    private Agente usuario;

    @Relationship("FEITO_POR")
    private Grupo grupo;
    

    public static Apontamento parse(ApontamentoDTO dto) {
        if (dto == null) 
            return null;
        
        Apontamento apontamento = new Apontamento();
        apontamento.setId(dto.id());
        apontamento.setTimestamp(dto.timestamp() == null ? null : DateTimeUtils.getZonedDateTime(dto.timestamp()));
        apontamento.setTexto(dto.texto());
        apontamento.setEtapa(Etapa.parse(dto.etapa()));
        apontamento.setCampo(Campo.parse(dto.campo()));
        apontamento.setUsuario(Agente.parse(dto.usuario()));
        apontamento.setGrupo(Grupo.parse(dto.grupo()));
        apontamento.setActive(dto.active());

        return apontamento;

    }


}
