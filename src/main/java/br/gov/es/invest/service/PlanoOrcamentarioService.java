package br.gov.es.invest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.projection.PlanoOrcamentarioDTOProjection;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.repository.PlanoOrcamentarioRepository;

@Service
public class PlanoOrcamentarioService {

    @Autowired
    private PlanoOrcamentarioRepository repository;

    public void saveAll(List<PlanoOrcamentario> planos) {
        repository.saveAll(planos);
    }

    public List<PlanoOrcamentarioDTOProjection> getAllSimples() {
        return repository.getAllSimples();
    }

}
