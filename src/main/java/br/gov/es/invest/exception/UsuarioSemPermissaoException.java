package br.gov.es.invest.exception;

public class UsuarioSemPermissaoException extends RuntimeException {

    public UsuarioSemPermissaoException() {
        super("Usuário sem permissão.");
    }
}
