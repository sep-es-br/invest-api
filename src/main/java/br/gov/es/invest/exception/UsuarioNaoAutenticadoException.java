package br.gov.es.invest.exception;

public class UsuarioNaoAutenticadoException extends RuntimeException {
    
    public UsuarioNaoAutenticadoException() {
        super("Usuario não autenticado, favor fazer o login!");
    }
}
