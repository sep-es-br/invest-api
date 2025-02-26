package br.gov.es.invest.dto;

import br.gov.es.invest.model.Parecer;
import br.gov.es.invest.utils.DateTimeUtils;

public record ParecerDTO(
    String id,
    EtapaDTO etapa,
    UsuarioDto feitoPor,
    GrupoDTO doGrupo,
    String timestamp,
    String texto
) {
    public static ParecerDTO parse(Parecer model) {
        return model == null ? null
        : new ParecerDTO(
            model.getId(), 
            EtapaDTO.parse(model.getEtapa()), 
            UsuarioDto.parse(model.getUsuario()), 
            GrupoDTO.parse(model.getGrupo()), 
            DateTimeUtils.formatZonedDateTime(model.getTimestamp()), 
            model.getTexto()
        );
    }
}
