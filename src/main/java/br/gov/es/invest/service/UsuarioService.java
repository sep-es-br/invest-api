package br.gov.es.invest.service;


import br.gov.es.invest.dto.usuario.SalvarUsuarioForm;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;
    
    @Autowired
    private GrupoService grupoSrv;
        
    public Agente save(Agente usuario) {
        
        Optional<Agente> usuarioBanco = getUserBySub(usuario.getSub());
        usuario.setId(usuarioBanco.map(Agente::getId).orElse(null));
        
        return repository.save(usuario);
    } 
    
    public Agente save(SalvarUsuarioForm slvUserForm) {
        
        Agente usuarioBanco = getUserBySub(slvUserForm.sub())
                                .orElseThrow(() -> new RuntimeException("Usuario com sub " + slvUserForm.sub() + " não encontrado"));
        
        
        usuarioBanco.set(slvUserForm);
        
        return this.save(usuarioBanco);
    } 
    
    public Optional<Agente> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Agente> getUserBySub(String sub){

        Agente probe = new Agente();
        probe.setSub(sub);

        Example<Agente> example = Example.of(probe);

        return this.repository.findBy(example, query -> query.first());

    }
    
    public List<Agente> findAll() {
        return this.repository.findAll(Sort.by("name"));
    }
    
    public Page<Agente> findAllPaged(String term, PageRequest pgRequest) {
        return this.repository.findAgentesSimples(term, pgRequest);
    }
    
    public Agente removerAgente(Long idAgente){
        
        this.grupoSrv.limparGruposDoAgente(idAgente);
        
        Agente agente = this.repository.findById(idAgente).orElseThrow();
        agente.setDeletadoEm(LocalDateTime.now());
               
        return this.repository.save(agente);
    }
    

}
