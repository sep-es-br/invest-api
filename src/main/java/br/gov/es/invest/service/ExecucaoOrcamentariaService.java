package br.gov.es.invest.service;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.DataMock;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ExecucaoOrcamentariaRepository;

@Service
public class ExecucaoOrcamentariaService {
    
    @Autowired
    private ExecucaoOrcamentariaRepository repository;

    public void saveAll(List<ExecucaoOrcamentaria> execucoesOrcamentarias) {
        repository.saveAll(execucoesOrcamentarias);
    }


    public List<ExecucaoOrcamentaria> getAll() {
        ArrayList<ExecucaoOrcamentaria> todasExecucoes = new ArrayList<>();


        DataMock.noPlanoOrcamentarios.forEach(po -> {
            po.getUnidadesOrcamentariaControladoras().forEach(uo -> {
                ExecucaoOrcamentaria exec = findByUOPO(uo, po);
                if(exec == null) {

                    String ano = String.valueOf(LocalDate.now().getYear()+1) ;

                    exec = new ExecucaoOrcamentaria(ano, 0, 0, null, null);

                }
            });
        });


        return todasExecucoes;
    }

    public ExecucaoOrcamentaria findByUOPO(UnidadeOrcamentaria uo, PlanoOrcamentario po){

        List<ExecucaoOrcamentaria> result = DataMock.noExecucaoOrcamentarias.stream()
                    .filter(eo -> {
                        return eo.getPlanoOrcamentarioOrientador().getCodigo().equals(po.getCodigo())
                            && eo.getUnidadeOrcamentariaImplementadora().getCodigo().equals(uo.getCodigo());
                    }).toList();
        
        return result.isEmpty() ? null : result.get(0);

    }

    public ExecucaoOrcamentaria findOrCreateByUOPO(String ano, UnidadeOrcamentaria uo, PlanoOrcamentario po, Conta conta){

        List<ExecucaoOrcamentaria> result = DataMock.noExecucaoOrcamentarias.stream()
                    .filter(eo -> {
                        return eo.getPlanoOrcamentarioOrientador().getCodigo().equals(po.getCodigo())
                            && eo.getUnidadeOrcamentariaImplementadora().getCodigo().equals(uo.getCodigo())
                            && eo.getContaDelimitada().getId().equals(conta.getId())
                            && eo.getAnoExercicio().equals(ano);
                    }).toList();
        
        return result.isEmpty() 
        ? new ExecucaoOrcamentaria(ano, po, uo, conta)
        : result.get(0);

    }

    public Double getTotalOrcadoByAno(String ano) {
        return repository.getTotalOrcadoByAno(ano);
    }
}
