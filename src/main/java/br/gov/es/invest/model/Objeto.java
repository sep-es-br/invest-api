package br.gov.es.invest.model;

import java.io.Serializable;
import java.security.cert.CertPath;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
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
    private String infoComplementares;
    private String contrato;

    @Relationship(type = "EM")
    private EmStatus emStatus;

    @Relationship(type = "EM")
    private EmEtapa emEtapa;

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

    @Relationship("POSSUI")
    private List<Apontamento> apontamentos;

    public Objeto(ObjetoDto dto) {
        this.setId(dto.id());
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.tipo = dto.tipo();
        this.emStatus = EmStatus.parse(dto.emStatus());
        this.emEtapa = EmEtapa.parse(dto.emEtapa());
        this.conta = Conta.parse(dto.conta());
        
        this.infoComplementares = dto.infoComplementares();
        this.contrato = dto.contrato();

        this.areaTematica = dto.areaTematica() == null ? null : new AreaTematica(dto.areaTematica());
        this.tiposPlano = dto.planos() == null ? null : dto.planos().stream().map(tipoDto -> new TipoPlano(tipoDto)).toList();
        this.responsavel = dto.responsavel() == null ? null : new Usuario(dto.responsavel());
        this.custosEstimadores = new ArrayList<>(dto.recursosFinanceiros().stream().map(custoDto -> new Custo(custoDto)).toList());
        this.microrregiao = dto.microregiaoAtendida() == null ? null : new Localidade(dto.microregiaoAtendida());
        this.apontamentos = dto.apontamentos() == null ? null : dto.apontamentos().stream().map(Apontamento::parse).toList();
        
    }

    public void filtrar(Integer anoExercicio, String fonteId) {
        

        if(anoExercicio != null){
            List<Custo> custos = this.getCustosEstimadores();
            custos = custos.stream().filter(custo -> custo.getAnoExercicio().equals(anoExercicio)).toList();
            this.setCustosEstimadores(new ArrayList<>(custos));
        }

        if(fonteId != null){
            for(Custo custo : this.getCustosEstimadores()) {

                custo.setIndicadaPor(
                    custo.getIndicadaPor().stream()
                    .filter(ip -> ip.getFonteOrcamentaria().getId().equals(fonteId) )
                    .collect(Collectors.toSet())
                );

            }

        }
        
        conta.filtrarExecucoes(anoExercicio, fonteId);
    }

    public static Objeto parse(ObjetoTiraProjection projection) {
        if(projection == null)
            return null;

        Objeto obj = new Objeto();
        obj.setId(projection.getId());
        obj.setNome(projection.getNome());
        obj.setTipo(projection.getTipo());
        obj.setEmStatus(projection.getEmStatus());
        obj.setEmEtapa(EmEtapa.parse(projection.getEmEtapa()));
        obj.setCustosEstimadores(projection.getCustosEstimadores());
        obj.setConta(projection.getConta());

        return obj;
    }

    public static Objeto parse(ObjetoDto dto) {
        return dto == null ? null
        : new Objeto(dto);
    }

}
