package br.gov.es.invest.model;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@Node
@SuperBuilder
public class Comando extends NoEntidade {
    

}
