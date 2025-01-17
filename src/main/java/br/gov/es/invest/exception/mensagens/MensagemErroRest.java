package br.gov.es.invest.exception.mensagens;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public record MensagemErroRest (
    HttpStatus status,
    Integer codigo,
    String mensagem,
    List<String> erros) {

    public MensagemErroRest(HttpStatus status, String mensagem, List<String> erros) {
        this(status, status.value(), mensagem, erros);
    }

    public static ResponseEntity<MensagemErroRest> asResponseEntity(HttpStatus status, String mensagem, List<String> erros){

        MensagemErroRest msg = new MensagemErroRest(status, mensagem, erros);

        return ResponseEntity
                .status(status)
                .body(msg);

    }
}
