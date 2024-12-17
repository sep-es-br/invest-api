package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.IValoresCusto;
import br.gov.es.invest.dto.ValoresCusto;
import br.gov.es.invest.dto.projection.IValoresIndicadaPor;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.IndicadaPor;
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

    public ValoresCusto getValoresTotais(String idFonte, Integer exercicio, String idUnidade, String idPlano){
        List<Custo> valores = repository.getTotais(idFonte, exercicio, idUnidade, idPlano);

        Double totalPrevisto = 0d;
        Double totalContratado = 0d;

        for(Custo valor : valores) {
            for(IndicadaPor valorIndicadaPor : valor.getIndicadaPor()){
                
                totalPrevisto += valorIndicadaPor.getPrevisto();
                totalContratado += valorIndicadaPor.getContratado();                
            }

        }

        return new ValoresCusto(totalPrevisto, totalContratado);
    }


}