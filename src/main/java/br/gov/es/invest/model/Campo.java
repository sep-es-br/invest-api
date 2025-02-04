package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.CampoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Node
public class Campo extends Entidade {

    public String campoId;
    public String nome;

    public Campo(String campoId, String nome){
        this.campoId = campoId;
        this.nome = nome;
    }

    public static Campo parse(CampoDTO dto) {
        if(dto == null)
            return null;

        Campo campo = new Campo();
        campo.setId(dto.id());
        campo.setCampoId(dto.campoId());
        campo.setNome(dto.nome());

        return campo;

    }

}
