package br.gov.es.invest.dto.projection;

import java.util.ArrayList;


import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.EmStatus;

public interface ObjetoTiraProjection {
    public String getId();
    public String getNome();
    public String getTipo();
    public EmStatus getEmStatus();
    public EmEtapaProjection getEmEtapa();
    public ArrayList<Custo> getCustosEstimadores();
    public Conta getConta();

}
