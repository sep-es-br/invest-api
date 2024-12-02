package br.gov.es.invest.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
        logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.UNAUTHORIZED, e.getMessage(), e.getErrors());
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(UsuarioSemPermissaoException.class)
    private ResponseEntity<MensagemErroRest> usuarioSemPermissaoHandler(UsuarioSemPermissaoException e){
        logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.FORBIDDEN, e.getLocalizedMessage(), Arrays.asList(e.getLocalizedMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(UsuarioInexistenteException.class)
    private ResponseEntity<MensagemErroRest> usuarioInexistenteHandler(UsuarioInexistenteException e) {
        logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage(), Collections.singletonList(e.getLocalizedMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    private ResponseEntity<MensagemErroRest> usuarioNaoAutenticadoHandler(UsuarioNaoAutenticadoException e) {
        logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage(), Collections.singletonList(e.getLocalizedMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(GrupoNaoEncotradoException.class)
    private ResponseEntity<MensagemErroRest> grupoNaoEncontradoHandler(GrupoNaoEncotradoException e){
        logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        MensagemErroRest mensagem = new MensagemErroRest(HttpStatus.NOT_FOUND, e.getLocalizedMessage(), Collections.singletonList(e.getLocalizedMessage()));
        return montarRetorno(mensagem);
    }


    private ResponseEntity<MensagemErroRest> montarRetorno(MensagemErroRest mensagem) {
        
        return ResponseEntity.status(mensagem.status()).body(mensagem);

    }

}
