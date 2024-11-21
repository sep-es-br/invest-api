package br.gov.es.invest.exception;

import java.util.Collections;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.exception.service.InfoplanServiceException;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = Logger.getLogger("RestExceptionHandler");


    @ExceptionHandler(InfoplanServiceException.class)
    private ResponseEntity<MensagemErroRest> infoplanServiceHandler(InfoplanServiceException e) {
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.UNAUTHORIZED, e.getMessage(), e.getErrors());
        return montarRetorno(mensagem);
    }


    private ResponseEntity<MensagemErroRest> montarRetorno(MensagemErroRest mensagem) {
        logger.severe(mensagem.toString());
        return ResponseEntity.status(mensagem.status()).body(mensagem);

    }

}
