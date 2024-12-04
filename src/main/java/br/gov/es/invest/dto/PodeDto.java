package br.gov.es.invest.dto;

import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.model.Pode;

public record PodeDto(
    String id,
    ModuloDto modulo,
    boolean listar,
    boolean visualizar,
    boolean criar,
    boolean editar, 
    boolean excluir
) {
    
    public PodeDto (Pode model) {
        this(
            model.getId(), 
            new ModuloDto(model.getModulo()), 
            model.isListar(), 
            model.isVisualizar(), 
            model.isCriar(), 
            model.isEditar(), 
            model.isExcluir()
        );
    }

}
