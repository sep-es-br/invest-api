package br.gov.es.invest.model;

import java.util.Optional;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import br.gov.es.invest.dto.SetorDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node
public class Setor extends Entidade {
    private String guid;
    private String nome;
    private String sigla;

    @Relationship(type = "PERTENCE_A")
    private Orgao orgao;

    

    public Setor(SetorDto dto) {
        this.setId(dto.id());
        this.guid = dto.guid();
        this.nome = dto.nome();
        this.sigla = dto.sigla();
        this.orgao = Orgao.parse(dto.orgao());
    }

    public static Setor parse(SetorDto dto) {
        return Optional.ofNullable(dto).map(Setor::new).orElse(null);
    }

    public static Setor parse(UnidadeACResponseDto unidadeAc, Orgao orgao) {
        if(unidadeAc == null) {
            return null;
        }

        return new Setor(
            unidadeAc.guid(),
            unidadeAc.nome(),
            unidadeAc.sigla(),
            orgao
        );
        
    }


}
