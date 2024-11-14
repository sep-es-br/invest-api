package br.gov.es.invest.dto;

public class ValoresTotaisCusto {
    private Double previsto;
    private Double contratado;

    public ValoresTotaisCusto () {

    }

    public ValoresTotaisCusto(Double previsto, Double contratado) {
        this.previsto = previsto;
        this.contratado = contratado;
    }

    public Double getContratado() {
        return contratado;
    }

    public Double getPrevisto() {
        return previsto;
    }

    public void setContratado(Double contratado) {
        this.contratado = contratado;
    }

    public void setPrevisto(Double previsto) {
        this.previsto = previsto;
    }
}
