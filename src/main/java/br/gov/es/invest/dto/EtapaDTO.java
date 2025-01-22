package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Etapa;

public record EtapaDTO(
    String id,
    Integer ordem,
    String nome,
    String status,
    GrupoDTO grupoResponsavel,
    List<AcaoDTO> acoes
) {
    public static EtapaDTO parse(Etapa model){
        if(model == null) return null;

        return new EtapaDTO(
            model.getId(), 
            model.getOrdem(), 
            model.getNome(), 
            model.getStatus(), 
            GrupoDTO.parse(model.getGrupoResponsavel()), 
            model.getAcoes() == null ? null : model.getAcoes().stream().map(AcaoDTO::parse).toList()
        );


    }
}
