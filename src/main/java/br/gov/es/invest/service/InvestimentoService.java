package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.InvestimentoRepository;

@Service
public class InvestimentoService {
    
    @Autowired
    private InvestimentoRepository repository;


    public void saveAll(List<Investimento> investimentos) {
        repository.saveAll(investimentos);
    }

    public List<Investimento> findAllByFilter(
            String nome, String codUnidade, String codPO,
            Integer exercicio, String idFonte, Pageable pageable
        ) {

        return repository.findAllByFilter(nome, codUnidade, codPO, exercicio, idFonte, pageable);
    }

    public int ammountByFilter(
            String nome, String codUnidade, String codPO, String exercicio, String idFonte
        ){
        return repository.countByFilter(nome, codUnidade, codPO, exercicio, idFonte);
    }

    public Investimento getByObjetoId(String objetoId){
        Investimento investimentoProbe = new Investimento();
        Objeto objetoProbe = new Objeto();

        objetoProbe.setId(objetoId);

        investimentoProbe.setObjetos(Set.of(objetoProbe));

        Example<Investimento> example = Example.of(investimentoProbe);
        
        Investimento result = repository.findBy(example, query -> query.oneValue());

        return result;


    }

    public Optional<Investimento> getByCodUoPo(String codUo, String codPo) {

        PlanoOrcamentario probePlano = new PlanoOrcamentario();
        probePlano.setCodigo(codPo);

        UnidadeOrcamentaria probeUnidade = new UnidadeOrcamentaria();
        probeUnidade.setCodigo(codUo);
        
        Investimento probeInvestimento = new Investimento();
        probeInvestimento.setPlanoOrcamentarioOrientador(probePlano);
        probeInvestimento.setUnidadeOrcamentariaImplementadora(probeUnidade);


        return repository.findBy(Example.of(probeInvestimento), query -> query.first());
    }
}
