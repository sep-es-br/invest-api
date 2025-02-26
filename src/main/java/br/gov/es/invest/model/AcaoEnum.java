package br.gov.es.invest.model;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.service.ObjetoService;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public enum AcaoEnum {


    APROVAR,
    DEVOLVER,
    INCLUIR_PO,
    REENVIAR,
    EXCLUIR_SOLICITACAO, APROVAR_SOLICITACAO, DEVOLVER_SOLICITACAO, APONTAMENTO

}
