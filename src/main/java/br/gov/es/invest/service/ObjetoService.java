package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
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
        return repository.save(objeto);
    }

    public List<Objeto> getAllByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String status, Pageable pageable) {
        List<Objeto> objsFiltrados = repository.getAllByFilter(exercicio, nome, idUnidade, idPo, status, pageable);
        
        return repository.findAllById(objsFiltrados.stream().map(obj -> obj.getId()).toList());
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

}
