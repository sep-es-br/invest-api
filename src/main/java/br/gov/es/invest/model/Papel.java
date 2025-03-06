package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Papel extends Entidade{
    
    private String nome;
    private String guid;
    private Boolean prioritario;

    @Relationship("ATUA_EM")
    private Setor setor;

    public static Papel parse(PapelDto papelDto){
        if(papelDto == null)
            return null;
        
        
        Papel papel = new Papel(
            papelDto.nome(), 
            papelDto.guid(), 
            papelDto.prioritario(), 
            papelDto.setor() == null ? null : new Setor(papelDto.setor())
        );
        papel.setId(papelDto.id());

        return papel;

    }

    public static Papel parse(PapelACResponseDto resp) {
        if(resp == null)
            return null;
        
        return new Papel(
            resp.Nome(), 
            resp.Guid(), 
            resp.Prioritario(), 
            null
        );
    }


}
