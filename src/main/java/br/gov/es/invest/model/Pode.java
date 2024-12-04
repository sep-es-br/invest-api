package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import br.gov.es.invest.dto.PodeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@RelationshipProperties
public class Pode extends Entidade {
    
    @TargetNode()
    private Modulo modulo;

    private boolean listar;
    private boolean visualizar;
    private boolean criar;
    private boolean editar;
    private boolean excluir;
    
    public Pode(PodeDto dto) {
        this.setId(dto.id());
        this.modulo = new Modulo(dto.modulo());
        this.listar = dto.listar();
        this.visualizar = dto.visualizar();
        this.criar = dto.criar();
        this.editar = dto.editar();
        this.excluir = dto.excluir();
    }
}
