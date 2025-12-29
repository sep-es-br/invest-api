package br.gov.es.invest.service;

import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.repository.PlanoOrcamentarioRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
public class PlanoOrcamentarioService {

    @Autowired
    private PlanoOrcamentarioRepository repository;

    @Autowired
    private Neo4jOperations neo4jOperations;

    @Autowired
    private PlanoOrcamentarioBIService planoOrcamentarioBIService;

    public void saveAll(List<PlanoOrcamentario> planos) {
        repository.saveAll(planos);
    }

    public List<PlanoOrcamentario> getAllSimples() {
        return repository.getAllSimples();
    }

    public String getCodById(Long idPlano) {
        return repository.getCodById(idPlano);
    }

    public Long getIdByCod(String cod) {
        return repository.findBy(
            Example.of(PlanoOrcamentario.builder().codigo(cod).build()), 
            q -> q.first())
            .map(PlanoOrcamentario::getId)
            .orElse(null);
    }
    
    public Optional<PlanoOrcamentario> getByCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Map<String, String> getIdsByCod(List<String> cods) {
        
        String cypher = """
                UNWIND $codigos AS codigo
                MATCH (p:PlanoOrcamentario {codigo: codigo})
                RETURN p.codigo AS cod, id(p) AS id
                """;
        
        List<CodIds> ids = neo4jOperations.findAll(cypher, Map.of("codigos", cods), CodIds.class);

        return ids.stream()
                .collect(Collectors.toMap(
                    CodIds::cod,
                    CodIds::id
                ));
    }

    public void atualizarNomesComBi() {
        List<PlanoOrcamentario> planos = repository.findAll();

        for(PlanoOrcamentario plano : planos){
            
            PlanoOrcamentario planoBi = planoOrcamentarioBIService.getPlanoPorCod(plano.getCodigo());
            if(planoBi == null){
                Logger.getGlobal().severe( String.format("plano cod. %s não encontrado", plano.getCodigo()));
            } else {
                Logger.getGlobal().info( String.format("%s: %s ==> %s", plano.getCodigo(), plano.getNome(), planoBi.getNome()));
                
                plano.setNome(planoBi.getNome());
            }
        }

        repository.saveAll(planos);
    }


    public PlanoOrcamentario findOrCreateByCod(PlanoOrcamentario plano){
        
        PlanoOrcamentario probe = new PlanoOrcamentario();
        probe.setCodigo(plano.getCodigo());
        
        Optional<PlanoOrcamentario> optPlano = repository.findBy( Example.of(probe), query -> query.first());

        return optPlano.orElse(plano);        


    }

}

record CodIds(String cod, String id) {

}
