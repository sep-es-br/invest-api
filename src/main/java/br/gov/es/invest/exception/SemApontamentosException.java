package br.gov.es.invest.exception;

public class SemApontamentosException extends Exception {
    
    public SemApontamentosException(){
        super("Nenhum apontamento existentes");
    }

}
