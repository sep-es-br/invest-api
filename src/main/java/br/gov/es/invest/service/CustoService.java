package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.repository.CustoRepository;

@Service
public class CustoService {
    
    @Autowired
    private CustoRepository repository;

    private ObjetoService objetoService;


    public void saveAll(List<Custo> custos) {
        repository.saveAll(custos);
    }


    @Autowired
    public void setObjetoService(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }

    


}