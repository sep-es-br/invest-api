package br.gov.es.invest.dto;

import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.utils.DateTimeUtils;

public record ApontamentoDTO(
    String id,
    String timestamp,
    String texto,
    EtapaDTO etapa,
    CampoDTO campo,
    UsuarioDto usuario,
    GrupoDTO grupo
) {

    public static ApontamentoDTO parse(Apontamento model) {
        return model == null ? null
        : new ApontamentoDTO(
            model.getId(), 
            DateTimeUtils.formatZonedDateTime(model.getTimestamp()), 
            model.getTexto(), 
            EtapaDTO.parse(model.getEtapa()), 
            CampoDTO.parse(model.getCampo()), 
            UsuarioDto.parse(model.getUsuario()), 
            GrupoDTO.parse(model.getGrupo())
        );

    }

} 
