package br.gov.es.invest.dto;

import br.gov.es.invest.model.Localidade;

public record LocalidadeDto(
    String id,
    String nome
) {
    
    public LocalidadeDto(Localidade model){
        this(model.getId(), model.getNome());
    }

}
