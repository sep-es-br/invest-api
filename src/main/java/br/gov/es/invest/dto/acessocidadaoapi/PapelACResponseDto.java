package br.gov.es.invest.dto.acessocidadaoapi;

public record PapelACResponseDto(
    String Guid,
    String Nome,
    String Tipo,
    String LotacaoGuid,
    String AgentePublicoSub,
    String AgentePublicoNome,
    Boolean Prioritario
) {
    
}
