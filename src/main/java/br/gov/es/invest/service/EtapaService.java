package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.EtapaEnum;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.repository.EtapaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtapaService {
    
    
    private final EtapaRepository etapaRepository;

    private final GrupoService grupoService;

    public List<Etapa> findAll(){
        return etapaRepository.findAll();
    }

    public Optional<Etapa> findById(String id) {
        return etapaRepository.findById(id);
    }
    
    public Optional<Etapa> getByEtapaId(String etapaId) {
        
        Etapa example = Etapa.builder().etapaId(EtapaEnum.valueOf(etapaId)).build();
        
        return etapaRepository.findBy(Example.of(example), q -> q.first());
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

    

}
