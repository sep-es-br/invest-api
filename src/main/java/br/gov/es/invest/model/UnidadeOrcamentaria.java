package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Node
public class UnidadeOrcamentaria extends Entidade implements Serializable {
    
    private Long codigo;
    private String guid;
    private String sigla;
    private String nome;

    @Relationship(type = "CONTROLA", direction = Direction.OUTGOING)
    private ArrayList<PlanoOrcamentario> planosOrcamentariosControlados = new ArrayList<>();

    @Relationship(type = "IMPLEMENTA", direction = Direction.OUTGOING)
    private ArrayList<Conta> ContasImplementadas = new ArrayList<>();


    public UnidadeOrcamentaria(Long codigo, String sigla, List<PlanoOrcamentario> planoOrcamentarios, List<Conta> execucoes) {
        this.codigo = codigo;
        this.sigla = sigla;
        planoOrcamentarios.forEach(this.planosOrcamentariosControlados::add);
        execucoes.forEach(this.ContasImplementadas::add);
    }

    public UnidadeOrcamentaria(UnidadeOrcamentariaDTO dto) {
        this.setId(dto.id());
        this.guid = dto.guid();
        this.nome = dto.nome();
        this.sigla = dto.sigla();
    }

}
