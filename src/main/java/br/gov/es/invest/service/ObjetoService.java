package br.gov.es.invest.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ObjetoRepository;

@Service
public class ObjetoService {
    
    @Autowired
    private ObjetoRepository repository;

    public void saveAll(List<Objeto> objetos) {
        repository.saveAll(objetos);
    }

    public Objeto save(Objeto objeto) {

        if(objeto.getId() != null) {
            List<TipoPlano> filhoAtuais = repository.findById(objeto.getId()).get().getTiposPlano();

            List<String> idsFilhoFinal = objeto.getTiposPlano().stream().map( filho -> filho.getId()).toList();

            List<TipoPlano> orfaos = filhoAtuais.stream().filter(filho -> !idsFilhoFinal.contains(filho.getId())).toList();

            if (!orfaos.isEmpty()) {
                repository.removerTipos(orfaos.stream().map(orfao -> orfao.getId()).toList());
            }

        }

        return repository.save(objeto);
    }

    public List<Objeto> getAllByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String status, Pageable pageable) {
        
        
        
        
        List<Objeto> objsFiltrados = repository.getAllByFilter(exercicio, nome, idUnidade, idPo, status, pageable);
        
        List<Objeto> objsHidratados = repository.findAllById(objsFiltrados.stream().map(obj -> obj.getId()).toList());

        for(Objeto objeto: objsHidratados) {
            objeto.filtrar(exercicio, null);
        }

        return objsHidratados;
    }

    public List<Objeto> findByFilter(
        String nome, String unidadeId, String planoId,
        Integer anoExercicio, String fonteId
    ) {

        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta();
        objetoProbe.setConta(contaProbe);

        if(nome != null) {
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("conta.nome", ExampleMatcher.GenericPropertyMatchers.ignoreCase().contains());
        }

        if(unidadeId != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(unidadeId);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        if(planoId != null) {
            if(planoId.equals("S.PO")) {
                matcher = matcher.withMatcher("conta.planoOrcamentario", ExampleMatcher.GenericPropertyMatchers.exact()).withIncludeNullValues();
            } else {
                PlanoOrcamentario planoProbe = new PlanoOrcamentario();
                planoProbe.setId(planoId);
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe));

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(anoExercicio, fonteId);
        }

        return objetoFiltrado;
    }

    public Objeto getByCusto(Custo custo){
        return repository.getByCusto(custo.getId());
    }

    public Optional<Objeto> getById(String id) {
        return repository.findById(id);
    }

    public List<Objeto> getAllByIds(List<String> ids) {
        return repository.findAllById(ids);
    }

    public int countByFilter(String nome, String codUnidade, String codPO, String status, Integer exercicio) {

        return repository.countByFilter(nome, codUnidade, codPO, status, exercicio);
    }

    public int countByInvestimentoFilter(String nome, String codUnidade, String codPO, Integer exercicio) {
        return repository.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio);
    }

    public List<String> findStatusCadastrados() {
        return repository.findStatusCadastrados();
    }

    public void removerObjeto(String objetoId) {
        repository.removerObjeto(objetoId);
    }

    public List<Objeto> findObjetoByConta(Conta conta) {
        Objeto objetoProbe = new Objeto();
        Conta contaProbe = new Conta();
        contaProbe.setId(conta.getId());
        objetoProbe.setConta(contaProbe);

        return repository.findAll(Example.of(objetoProbe));
    }

    public List<Objeto> findObjetoByContaFiltrado(Conta conta, Integer exercicio, String fonteId) {
        List<Objeto> todosObjetos = findObjetoByConta(conta);

        for(Objeto objeto : todosObjetos) {
            objeto.filtrar(exercicio, fonteId);
        }

        return todosObjetos;
    }

}
