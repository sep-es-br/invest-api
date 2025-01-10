package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.ObjetoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Objeto extends Entidade implements Serializable {
    
    private String nome;
    private String descricao;
    private String tipo;
    private int openPMOId;
    private String status;
    private String infoComplementares;
    private Boolean objContratado;
    private Boolean audienciaPublica; 
    private Boolean estrategica;
    private Boolean cti;
    private Boolean climatica; 
    private Boolean pip;

    @Relationship(type = "ESTIMADO", direction = Direction.INCOMING)
    private ArrayList<Custo> custosEstimadores = new ArrayList<>();

    @Relationship(type = "ATENDE", direction = Direction.OUTGOING)
    private Localidade microrregiao;

    public Objeto(ObjetoDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.tipo = dto.tipo();
        this.status = (dto.planoOrcamentario() == null) ?  StatusObjetoEnum.EM_APROVACAO.getNome() : StatusObjetoEnum.CADASTRADO.getNome();
        this.infoComplementares = dto.infoComplementares();
        this.objContratado = dto.objContratado();
        this.audienciaPublica = dto.audienciaPublica();
        this.estrategica = dto.estrategica();
        this.cti = dto.cti();
        this.climatica = dto.climatica();
        this.pip = dto.pip();

        this.custosEstimadores = new ArrayList<>(dto.recursosFinanceiros().stream().map(custoDto -> new Custo(custoDto)).toList());
        this.microrregiao = new Localidade(dto.microregiaoAtendida());
    }

}
