package br.gov.es.invest.exception;

public class GrupoNaoEncotradoException extends RuntimeException {
    
    public GrupoNaoEncotradoException(Long id) {
        super("Grupo com id " + id + " não encontrado");
    }

}
