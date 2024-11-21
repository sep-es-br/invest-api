package br.gov.es.invest.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ValoresTotaisCusto;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.repository.CustoRepository;

@Service
public class CustoService {
    
    @Autowired
    private CustoRepository repository;


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


}