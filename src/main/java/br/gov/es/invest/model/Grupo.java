package br.gov.es.invest.model;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.GrupoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Grupo extends Entidade {
    private String sigla;
    private String icone;
    private String nome;
    private String descricao;

    private List<MembroGrupo> membros; 

    public Grupo(GrupoDTO dto){
        this.setId(dto.getId());
        this.sigla = dto.getSigla();
        this.icone = dto.getIcone();
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
    }
}
