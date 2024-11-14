package br.gov.es.invest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ValoresTotaisCusto;
import br.gov.es.invest.exception.BatataException;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.DataMock;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.repository.CustoRepository;

@Service
public class CustoService {
    
    @Autowired
    private CustoRepository repository;

    private ObjetoService objetoService;

    public void saveAll(List<Custo> custos) {
        repository.saveAll(custos);
    }

    public List<Custo> getAllByExercicio(String exercicio){
        return repository.findByExercicio(exercicio);
    }

    public ValoresTotaisCusto getValoresTotais(String exercicio){
        return repository.getTotais(exercicio).get(0);
    }

    public Double getTotalPrevisto(String exercicio) {
        return repository.getTotaisPrevisto(exercicio);
    }

    public Double getTotalContratado(String exercicio){
        return repository.getTotaisContratado(exercicio);
    }

    public double totalPrevisto(String exercicio){

        double totalPrevisto = 0;

        List<Custo> custosAno = repository.findByExercicio(exercicio);

        for (Custo custo : custosAno) {
            totalPrevisto += custo.getPrevisto();
        }
        
        return totalPrevisto;

    }

    public double totalHomologado(String exercicio){

        double totalPrevisto = 0;

        List<Custo> custosAno = repository.findByExercicio(exercicio);

        for (Custo custo : custosAno) {
            totalPrevisto += custo.getContratado();
        }
        
        return totalPrevisto;

    }

    public double totalAutorizado(){

        double totalAutorizado = 0;

        List<ExecucaoOrcamentaria> ExecsAno = DataMock.noExecucaoOrcamentarias.stream()
                .filter(custo -> custo.getAnoExercicio().equals("2025"))
                .collect(Collectors.toList());

        for (ExecucaoOrcamentaria custo : ExecsAno) {
            totalAutorizado += custo.getAutorizado();
        }
        
        return totalAutorizado;

    }

    public double totalDisponivel(){

        double totalDisponivel = 0;

        List<ExecucaoOrcamentaria> ExecsAno = DataMock.noExecucaoOrcamentarias.stream()
                .filter(custo -> custo.getAnoExercicio().equals("2025"))
                .collect(Collectors.toList());

        // for (ExecucaoOrcamentaria custo : ExecsAno) {
        //     totalDisponivel += custo.getAutorizado();
        // }
        
        return totalDisponivel;

    }
    

}