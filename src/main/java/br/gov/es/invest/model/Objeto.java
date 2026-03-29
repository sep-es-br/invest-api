package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.util.comparator.Comparators;

@Getter
@Setter
@RequiredArgsConstructor
@Node
@SuperBuilder
public class Objeto extends NoEntidade implements Serializable {
    
    private Integer gnd;
    private String nome;
    private String hashProposta;
    private String descricao;
    private String tipo;
    private int openPMOId;
    private String infoComplementares;
    private String contrato;
    private String possuiOrcamento;
    
    private ZonedDateTime timestamp;

    @Relationship(type = "EM")
    private EmStatus emStatus;

    @Relationship(type = "EM")
    private List<EmEtapa> emEtapa = new ArrayList<>();

    @Relationship(type = "SOBRE", direction = Direction.OUTGOING)
    private AreaTematica areaTematica;

    @Relationship(type = "DO_TIPO", direction = Direction.OUTGOING)
    private List<TipoPlano> tiposPlano;

    @Relationship(type = "RESPONSAVEL_POR", direction = Direction.INCOMING)
    private Agente responsavel;

    @Relationship(type = "ESTIMADO", direction = Direction.INCOMING)
    private ArrayList<Custo> custosEstimadores = new ArrayList<>();

    @Relationship(type = "ATENDE", direction = Direction.OUTGOING)
    private Localidade microrregiao;

    @Relationship(type = "CUSTEADO")
    private Conta conta;

    @Relationship("POSSUI")
    private List<Apontamento> apontamentos;

    @Relationship("POSSUI")
    private List<Parecer> pareceres;
    
    @Relationship("REVISADO_POR")
    private List<RevisadoPor> revistoPor;
    
    @Relationship("ALTERADO_POR")
    private List<AlteradoPor> alteradoPor;

    public Objeto(ObjetoDto dto) {
        this.setId(dto.id());
        this.gnd = dto.gnd();
        this.nome = dto.nome();
        this.hashProposta = dto.hashProposta();
        this.descricao = dto.descricao();
        this.tipo = dto.tipo();
        this.emStatus = EmStatus.parse(dto.emStatus());
        this.emEtapa = Optional.ofNullable(dto.emEtapa()).map(l -> l.stream().map(EmEtapa::parse).toList()).orElse(null);
        this.conta = Conta.parse(dto.conta());
        
        this.infoComplementares = dto.infoComplementares();
        this.contrato = dto.contrato();

        this.possuiOrcamento = dto.possuiOrcamento();

        this.areaTematica = dto.areaTematica() == null ? null : new AreaTematica(dto.areaTematica());
        this.tiposPlano = dto.planos() == null ? null : dto.planos().stream().map(tipoDto -> new TipoPlano(tipoDto)).toList();
        this.responsavel = dto.responsavel() == null ? null : new Agente(dto.responsavel());
        this.custosEstimadores = new ArrayList<>(dto.recursosFinanceiros().stream().map(custoDto -> new Custo(custoDto)).toList());
        this.microrregiao = dto.microregiaoAtendida() == null ? null : new Localidade(dto.microregiaoAtendida());
        this.apontamentos = dto.apontamentos() == null ? null : dto.apontamentos().stream().map(Apontamento::parse).toList();
        this.pareceres = dto.pareceres() == null ? null : dto.pareceres().stream().map(Parecer::parse).toList();
        
    }
    
    public Objeto aplicar(Objeto src) {
        this.gnd = src.getGnd();
        this.nome = src.getNome();
        this.hashProposta = src.getHashProposta();
        this.descricao = src.getDescricao();
        this.tipo = src.getTipo();
        this.conta = src.getConta();
        
        this.infoComplementares = src.getInfoComplementares();
        this.contrato = src.getContrato();

        this.possuiOrcamento = src.getPossuiOrcamento();

        this.areaTematica = src.getAreaTematica();
        this.tiposPlano = src.getTiposPlano();
        this.custosEstimadores = src.getCustosEstimadores();
        this.microrregiao = src.getMicrorregiao();
        this.apontamentos = src.getApontamentos();
        this.pareceres = src.getPareceres();
        
        return this;
    }

    public void filtrar(Integer anoExercicio, Long fonteId) {
        

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
                    .collect(Collectors.toList())
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
        obj.setEmEtapa(Optional.ofNullable(projection.getEmEtapa()).map(l -> l.stream().map(EmEtapa::parse).toList()).orElse(null));
        obj.setCustosEstimadores(projection.getCustosEstimadores());
        obj.setConta(projection.getConta());

        return obj;
    }

    public static Objeto parse(ObjetoDto dto) {
        return dto == null ? null
        : new Objeto(dto);
    }
    
    
    public void setCustosEstimadoresFromDto(List<ObjetoCadastroFormDto.Custo> custos) {

       custos.forEach(custo -> {

           Custo custoModel = this.getCustosEstimadores().stream()
                   .filter(c -> c.getAnoExercicio().equals(custo.ano()))
                   .findFirst()
                   .orElseGet(() -> {
                       Custo novo = Custo.builder()
                               .anoExercicio(custo.ano())
                               .objeto(this)
                               .build();
                       this.getCustosEstimadores().add(novo);
                       return novo;
                   });

           custo.valoresFontes().forEach(vf -> {

               IndicadaPor ip = Optional.ofNullable(custoModel.getIndicadaPor())
                       .orElseGet(() -> {
                            List<IndicadaPor> ipList = new ArrayList<>();

                            custoModel.setIndicadaPor(ipList);

                            return ipList;
                        }).stream()
                       .filter(_ip -> _ip.getFonteOrcamentaria()
                               .getCodigo()
                               .equals(vf.fonte().getCodigo()))
                       .findFirst()
                       .orElseGet(() -> {
                           IndicadaPor novo = IndicadaPor.builder()
                                   .fonteOrcamentaria(new FonteOrcamentaria(vf.fonte()))
                                   .build();

                           custoModel.getIndicadaPor().add(novo);

                           return novo;
                       });

               ip.setContratado(Optional.ofNullable(vf.contratado()).orElse(0d));
               ip.setPlanejado(vf.planejado());
           });
       });
   }
    
    @Transient
    public EmEtapa getEtapaAtual() {
        if(this.getEmEtapa() == null || this.getEmEtapa().isEmpty()) {
            return null;
        }
        
        return this.getEmEtapa().stream()
                .max(Comparator.comparing(EmEtapa::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

}
