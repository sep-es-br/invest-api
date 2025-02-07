package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.repository.EtapaRepository;

@Service
public class EtapaService {
    
    @Autowired
    private EtapaRepository etapaRepository;

    private GrupoService grupoService;

    public List<Etapa> findAll(){
        return etapaRepository.findAll();
    }

    public Optional<Etapa> findById(String id) {
        return etapaRepository.findById(id);
    }

    public Etapa getEtapaDoUsuario(String userId) {
        List<Grupo> gruposDoUser = grupoService.getGruposDoUsuario(userId);

        Grupo grupoProbe = new Grupo();
        Etapa etapaProbe = new Etapa();
        etapaProbe.setGrupoResponsavel(grupoProbe);

        for(Grupo grupo : gruposDoUser) {
            grupoProbe.setId(grupo.getId());

            Optional<Etapa> optEtapa = etapaRepository.findBy(Example.of(etapaProbe), q -> q.first());

            if(optEtapa.isPresent()) {
                return optEtapa.get();
            }
        }

        return null;
    }

    @Autowired
    public void setGrupoService(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    

}
