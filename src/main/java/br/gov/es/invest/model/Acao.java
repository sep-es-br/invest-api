package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.AcaoDTO;
import br.gov.es.invest.dto.StatusDTO;
import br.gov.es.invest.service.EtapaService;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
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
