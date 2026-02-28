package br.gov.es.invest.service;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.repository.CustoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustoService {
    
    private final CustoRepository repository;

    public void saveAll(List<Custo> custos) {
        repository.saveAll(custos);
    }
    
    public Optional<Custo> findByAnoExercicio(Integer anoExercicio, Long idObjeto) {
        return this.repository.findIdByAnoExercicioObjetoId(anoExercicio, idObjeto)
                .flatMap(this.repository::findById);
    }
    
    
    


    


}