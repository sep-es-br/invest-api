package br.gov.es.invest.model;

import java.time.ZonedDateTime;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.ApontamentoDTO;
import br.gov.es.invest.service.EtapaService;
import br.gov.es.invest.utils.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
@Node
@SuperBuilder
public class Apontamento extends Entidade{
    
    private ZonedDateTime timestamp;
    private String texto;
    private boolean active;

    @Transient
    private String etapaId;
    
    @Relationship("EM")
    private Etapa etapa;
    
    @Relationship("SOBRE")
    private Campo campo;
    
    @Relationship("FEITO_POR")
    private Usuario usuario;

    @Relationship("FEITO_POR")
    private Grupo grupo;
    
    public Apontamento hidratar(EtapaService etapaSrv) {
        
        if(etapaId != null)
            this.etapa = etapaSrv.getByEtapaId(etapaId).orElseThrow();
        
        return this;
    }

    public static Apontamento parse(ApontamentoDTO dto) {
        if (dto == null) 
            return null;
        
        Apontamento apontamento = new Apontamento();
        apontamento.setId(dto.id());
        apontamento.setTimestamp(dto.timestamp() == null ? null : DateTimeUtils.getZonedDateTime(dto.timestamp()));
        apontamento.setTexto(dto.texto());
        apontamento.setEtapaId(dto.etapa());
        apontamento.setCampo(Campo.parse(dto.campo()));
        apontamento.setUsuario(Usuario.parse(dto.usuario()));
        apontamento.setGrupo(Grupo.parse(dto.grupo()));
        apontamento.setActive(dto.active());

        return apontamento;

    }


}
