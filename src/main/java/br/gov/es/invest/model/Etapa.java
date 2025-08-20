package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.EtapaDTO;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.utils.components.FluxoConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Etapa extends Entidade {
    
    private EtapaEnum etapaId;
    
    @Transient
    private String grupoResponsavelId;
    
    @Relationship(type = "RESPONSAVEL_POR", direction = Direction.INCOMING)
    private Grupo grupoResponsavel;

    public void hidratar(GrupoService grupoSrv) {
        if(grupoResponsavelId == null) return;
        
        this.grupoResponsavel = grupoSrv.findById(grupoResponsavelId).orElse(null);
    }
    
    public static Etapa parse(EtapaDTO dto) {
        if(dto == null)
            return null;

        Etapa etapa = new Etapa();
        etapa.setEtapaId(EtapaEnum.valueOf(dto.etapaId()));
        etapa.setGrupoResponsavelId(dto.grupoResponsavel());

        return etapa;

    }
    

}
