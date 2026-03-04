package br.gov.es.invest.service;

import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.EtapaEnum;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.repository.EtapaRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class EtapaService {
    
    @Autowired
    private EtapaRepository etapaRepository;

    @Autowired
    private Neo4jClient neo4jClient;
    
    private GrupoService grupoService;
    
    

    public List<Etapa> findAll(){
        return etapaRepository.findAll();
    }

    public Optional<Etapa> findById(Long id) {
        return etapaRepository.findById(id);
    }

    public Etapa getEtapaDoUsuario(Long userId) {
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
    
    

    public void addEmEtapa(Objeto objeto, EmEtapa emEtapa) {

        Assert.notNull(objeto.getId(), "Objeto não está salvo no Banco");
        Assert.notNull(emEtapa.getEtapa().getId(), "Etapa não está salvo no Banco");
        
        String cypher = """
                MATCH (objeto:Objeto), (etapa:Etapa)
                WHERE id(objeto) = $objetoId
                AND id(etapa) = $etapaId

                MERGE (objeto)-[rel:EM]->(etapa)
                SET rel.atividade = $atividade
                SET rel.devolvido = $devolvido
                SET rel.timestamp = $timestamp
                """;
        
        Map<String, Object> params = new HashedMap<>();
        params.put("objetoId", objeto.getId());
        params.put("etapaId", emEtapa.getEtapa().getId());
        params.put("atividade", emEtapa.getAtividade());
        params.put("devolvido", emEtapa.isDevolvido());
        params.put("timestamp", Optional.ofNullable(emEtapa.getTimestamp()).orElse(ZonedDateTime.now()));

        neo4jClient.query(cypher)
                    .bindAll(params)
                    .run();

    }
    
    public Etapa getEtapaByEtapaId(EtapaEnum etapa) {
        return etapaRepository.findByEtapaId(etapa).orElse(null);
    }

    @Autowired
    public void setGrupoService(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    

}
