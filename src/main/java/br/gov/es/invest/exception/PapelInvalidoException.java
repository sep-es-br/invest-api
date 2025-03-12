package br.gov.es.invest.exception;

public class PapelInvalidoException extends RuntimeException {
    

    public PapelInvalidoException(String motivo) {
        super("Papel invalido: " + motivo);
    }
}
