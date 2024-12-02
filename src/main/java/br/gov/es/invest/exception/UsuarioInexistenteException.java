package br.gov.es.invest.exception;

public class UsuarioInexistenteException extends RuntimeException {
    
    public UsuarioInexistenteException(){
        super("Usuario inexistente");
    }

}
