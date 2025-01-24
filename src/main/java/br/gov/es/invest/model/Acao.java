package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.AcaoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Acao extends Entidade {
    
    private String nome;

    private String IdProximaEtapa;

    public static Acao parse(AcaoDTO dto) {
        if(dto == null)
            return null;

        Acao acao = new Acao();
        acao.setId(dto.id());
        acao.setNome(dto.nome());
        acao.setIdProximaEtapa(dto.idProxEtapa());

        return acao;

    }

}
