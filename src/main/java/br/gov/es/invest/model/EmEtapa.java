package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.service.EtapaService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
@RelationshipProperties
@SuperBuilder
public class EmEtapa extends Entidade {
    
    @Transient
    private String etapaId;
    
    @TargetNode
    private Etapa etapa;

    private String atividade;

    private boolean devolvido;
    
    public void hidratar(EtapaService etapaSrv) {
        if(etapaId == null) return;
        
        this.etapa = etapaSrv.getByEtapaId(etapaId).orElse(null);
    }

    public static EmEtapa parse(EmEtapaDTO dto) {
        if(dto == null) {
            return null;
        }

        EmEtapa emEtapa = new EmEtapa();
        emEtapa.setId(dto.id());
        emEtapa.etapaId = dto.etapa().etapaId();
        emEtapa.atividade = dto.atividade();
        emEtapa.isDevolvido();

        return emEtapa;
    }

}
