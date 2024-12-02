package br.gov.es.invest.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MembroGrupo extends Entidade {
    
    private List<Grupo> membroDe;

}
