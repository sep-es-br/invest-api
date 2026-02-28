package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.AcaoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Node
@SuperBuilder
public class Acao extends Entidade {
    
    private String nome;
    private String atividadeFinal;
    private AcaoEnum acaoId;
    private Boolean positivo;

    @Relationship("GERA")
    private Status statusFinal;

    @Relationship("VAI_PARA")
    private Etapa proxEtapa;

    public static Acao parse(AcaoDTO dto, Etapa proxEtapa) {
        if(dto == null)
            return null;

        Acao acao = new Acao();
        acao.setId(dto.id());
        acao.setNome(dto.nome());
        acao.setAcaoId(AcaoEnum.valueOf(dto.acaoId()));
        acao.setAtividadeFinal(dto.atividadeFinal());
        acao.setPositivo(dto.positivo());
        acao.setStatusFinal(Status.parse(dto.statusFinal()));
        acao.setProxEtapa(proxEtapa);

        return acao;

    }

}
