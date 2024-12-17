package br.gov.es.invest.dto;

import java.util.Set;

import org.springframework.data.neo4j.core.schema.RelationshipId;

import br.gov.es.invest.dto.projection.IValoresIndicadaPor;
import br.gov.es.invest.model.Entidade;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface IValoresCusto {
    Set<IValoresIndicadaPor> getIndicadaPor(); 
}