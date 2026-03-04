package br.gov.es.invest.dto.projection;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.EmStatus;
import java.util.ArrayList;
import java.util.List;

public interface ObjetoTiraProjection {
    public Long getId();
    public String getNome();
    public String getTipo();
    public EmStatus getEmStatus();
    public List<EmEtapaProjection> getEmEtapa();
    public ArrayList<Custo> getCustosEstimadores();
    public Conta getConta();

}
