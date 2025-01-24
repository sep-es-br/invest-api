package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.EtapaDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Etapa extends Entidade {

    private Integer ordem;
    private String nome;
    private String status;
    private Grupo grupoResponsavel;

    private List<Acao> acoes;

    public static Etapa parse(EtapaDTO dto) {
        if(dto == null)
            return null;

        Etapa etapa = new Etapa();
        etapa.setId(dto.id());
        etapa.setOrdem(dto.ordem());
        etapa.setNome(dto.nome());
        etapa.setStatus(dto.status());
        etapa.setGrupoResponsavel(dto.grupoResponsavel() == null ? null : Grupo.parse(dto.grupoResponsavel()));

        etapa.setAcoes(dto.acoes() == null ? null : dto.acoes().stream().map(Acao::parse).toList());

        return etapa;

    }

}
