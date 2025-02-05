package br.gov.es.invest.model;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.FluxoDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Fluxo extends Entidade {
    
    private String nome;
    private String fluxoId;

    @Relationship("POSSUI")
    private List<Etapa> etapas;

    @Relationship("COMECA_EM")
    private Etapa etapaInicial;

    public List<Etapa> getEtapas() {
        return this.etapas == null ? null : this.etapas.stream().sorted((etapa1, etapa2) -> etapa1.getOrdem() - etapa2.getOrdem()).toList();
    }

    public static Fluxo parse(FluxoDTO dto) {
        if(dto == null) {
            return null;
        }

        Fluxo fluxo = new Fluxo();
        fluxo.setId(dto.id());
        fluxo.setNome(dto.nome());
        fluxo.setEtapas(dto.etapas() == null ? null : dto.etapas().stream().map(Etapa::parse).toList());

        return fluxo;

    }    

}
