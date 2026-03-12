package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.EtapaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Etapa extends NoEntidade {

    private Integer ordem;
    private String nome;
    private EtapaEnum etapaId;

    @Relationship(type = "RESPONSAVEL_POR", direction = Direction.INCOMING)
    private Grupo grupoResponsavel;

    @Relationship("EXECUTA")
    private List<Acao> acoes;

    public static Etapa parse(EtapaDTO dto) {
        if(dto == null)
            return null;

        Etapa etapa = new Etapa();
        etapa.setId(dto.id());
        etapa.setOrdem(dto.ordem());
        etapa.setNome(dto.nome());
        etapa.setEtapaId(dto.etapaId());
        etapa.setGrupoResponsavel( Grupo.parse(dto.grupoResponsavel()) );

        return etapa;

    }

}
