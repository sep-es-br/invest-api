package br.gov.es.invest.service;

import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.UnidadeOrcamentariaRepository;
import br.gov.es.invest.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class UnidadeOrcamentariaService {
    
    @Autowired
    private UnidadeOrcamentariaRepository repository;
    
    @Autowired 
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UnidadeOrcamentariaBIService biSrv;

    public List<UnidadeOrcamentariaDTOProjection> getAllSimples() {
        return repository.findAllUnidades();
    }
    
    public List<UnidadeOrcamentariaDTOProjection> getAllSimplesForFluxo() {
        return repository.findAllUnidadesForFluxo();
    }
    
    public String getCodById(Long idUnidade) {
        return repository.getCodById(idUnidade);
    }

    public List<String> getCodsByIds(List<Long> ids){
        return repository.getCodsById(ids);
    }
    
    public List<UnidadeOrcamentaria> findByAgente(Long agenteId){
        
        Agente agente = this.usuarioRepository.findById(agenteId).orElseThrow();
        
        List<String> orgaoIds = agente.getPapeis().stream()
                                .map(Papel::getSetor)
                                .map(Setor::getOrgao)
                                .map(Orgao::getCodigo)
                                .collect(Collectors.toList());
        
        return orgaoIds.stream().flatMap(id -> this.biSrv.getTodasUnidades(id).stream()).collect(Collectors.toList());
        
    }

    public Long getIdByCod(String cod) {
        
        return repository.findBy(
            Example.of( UnidadeOrcamentaria.builder().codigo(cod).build() ), 
            q -> q.first()
        ).map(UnidadeOrcamentaria::getId)
        .orElse(null);
        
    }
    
    public Optional<UnidadeOrcamentaria> getByCod(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public UnidadeOrcamentaria findOrCreateByCod(UnidadeOrcamentaria unidade){
        
        UnidadeOrcamentaria probe = new UnidadeOrcamentaria();
        probe.setCodigo(unidade.getCodigo());

        Optional<UnidadeOrcamentaria> optUnidade = repository.findBy(Example.of(probe), query -> query.first());

        return optUnidade.orElse(unidade);
    }
    
    public UnidadeOrcamentaria findOrCreateByCod(UnidadeOrcamentariaDTO unidadeDto){
        
        UnidadeOrcamentaria unidade = new UnidadeOrcamentaria(unidadeDto);
        
        unidade.setId(repository.findByCodigo(unidadeDto.codigo()).map(UnidadeOrcamentaria::getId).orElse(null));

        return unidade;
    }

    public List<UnidadeOrcamentaria> findByOrgaoId(Orgao orgao) {
        Orgao orgaoProbe = new Orgao();
        orgaoProbe.setId(orgao.getId());

        UnidadeOrcamentaria probe = new UnidadeOrcamentaria();
        probe.setOrgaoPai(orgaoProbe);

        return this.repository.findBy(Example.of(probe), q -> q.all());
    }


}
