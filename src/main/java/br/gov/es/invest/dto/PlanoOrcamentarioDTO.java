package br.gov.es.invest.dto;

import br.gov.es.invest.model.PlanoOrcamentario;

public record PlanoOrcamentarioDTO(
    Long id, 
    String codigo, 
    String nome
)  {
    
    public PlanoOrcamentarioDTO(PlanoOrcamentario model) {
        this(model.getId(), model.getCodigo(), model.getNome());
    }
       

}
