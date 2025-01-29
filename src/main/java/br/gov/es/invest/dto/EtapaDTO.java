package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.AcaoEnum;
import br.gov.es.invest.model.Etapa;

public record EtapaDTO(
    String id,
    Integer ordem,
    String nome,
    GrupoDTO grupoResponsavel,
    List<AcaoDTO> acoes
) {
    public static EtapaDTO parse(Etapa model){
        if(model == null) return null;

        return new EtapaDTO(
            model.getId(), 
            model.getOrdem(), 
            model.getNome(), 
            GrupoDTO.parse(model.getGrupoResponsavel()), 
            model.getAcoes().stream().map(AcaoDTO::parse).toList()
        );


    }
}
