package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String contrato;

    @Relationship(type = "SOBRE", direction = Direction.OUTGOING)
    private AreaTematica areaTematica;

    @Relationship(type = "DO_TIPO", direction = Direction.OUTGOING)
    private List<TipoPlano> tiposPlano;

    @Relationship(type = "RESPONSAVEL_POR", direction = Direction.INCOMING)
    private Usuario responsavel;

    @Relationship(type = "ESTIMADO", direction = Direction.INCOMING)
    private ArrayList<Custo> custosEstimadores = new ArrayList<>();

    @Relationship(type = "ATENDE", direction = Direction.OUTGOING)
    private Localidade microrregiao;

    @Relationship(type = "CUSTEADO")
    private Conta conta;

    public Objeto(ObjetoDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.tipo = dto.tipo();
        this.status = (dto.conta().planoOrcamentario() == null) ?  StatusObjetoEnum.EM_APROVACAO.getNome() : StatusObjetoEnum.CADASTRADO.getNome();
        this.infoComplementares = dto.infoComplementares();
        this.contrato = dto.contrato();

        this.areaTematica = dto.areaTematica() == null ? null : new AreaTematica(dto.areaTematica());
        this.tiposPlano = dto.planos() == null ? null : dto.planos().stream().map(tipoDto -> new TipoPlano(tipoDto)).toList();
        this.responsavel = dto.responsavel() == null ? null : new Usuario(dto.responsavel());
        this.custosEstimadores = new ArrayList<>(dto.recursosFinanceiros().stream().map(custoDto -> new Custo(custoDto)).toList());
        this.microrregiao = dto.microregiaoAtendida() == null ? null : new Localidade(dto.microregiaoAtendida());
        
    }

    public void filtrar(Integer anoExercicio) {
        List<Custo> custos = this.getCustosEstimadores();
        custos = custos.stream().filter(custo -> custo.getAnoExercicio().equals(anoExercicio)).toList();
        this.setCustosEstimadores(new ArrayList<>(custos));
        conta.filtrarExecucoes(anoExercicio);
    }

}
