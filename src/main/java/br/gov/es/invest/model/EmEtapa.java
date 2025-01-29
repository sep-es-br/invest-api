package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import br.gov.es.invest.dto.EmEtapaDTO;
import br.gov.es.invest.dto.projection.EmEtapaProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@RelationshipProperties
public class EmEtapa extends Entidade {
    
    @TargetNode
    private Etapa etapa;

    private String atividade;

    private boolean devolvido;

    public static EmEtapa parse(EmEtapaDTO dto) {
        if(dto == null) {
            return null;
        }

        EmEtapa emEtapa = new EmEtapa();
        emEtapa.setId(dto.id());
        emEtapa.etapa = Etapa.parse(dto.etapa());
        emEtapa.atividade = dto.atividade();
        emEtapa.isDevolvido();

        return emEtapa;
    }

    public static EmEtapa parse (EmEtapaProjection projection) {
        if(projection == null) return null;

        EmEtapa emEtapa = new EmEtapa();
        emEtapa.setId(projection.getId());

        return emEtapa;
    }

}
