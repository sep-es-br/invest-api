package br.gov.es.invest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.GrupoRepository;
import br.gov.es.invest.repository.OrgaoRepository;
import br.gov.es.invest.repository.SetorRepository;
import br.gov.es.invest.repository.UnidadeOrcamentariaRepository;
import br.gov.es.invest.repository.UsuarioRepository;

@Service
public class GrupoService {
    
    @Autowired
    private GrupoRepository repository;

    @Autowired
    private OrgaoRepository orgaoRepository;

    @Autowired
    private SetorRepository setorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Grupo> findAll(String nome, Pageable pageable) {
        
        return repository.findAllByFilter(nome, pageable);

    }

    public Optional<Grupo> findById(String id){
        return repository.findByIdHidratado(id);
    }

    public Grupo save(Grupo grupo){
        return repository.save(grupo);
    }

    public Grupo delete (String grupoId){
        Grupo deletedGrupo = repository.findById(grupoId).orElse(null);

        if(deletedGrupo != null) {
            repository.delete(deletedGrupo);
        }

        return deletedGrupo;
    }

    public void addMembro(Grupo grupo, Orgao orgao, Setor setor, PapelDto papelDto){
        
        Optional<Orgao> orgaoBanco = orgaoRepository.findByGuid(orgao.getGuid());

        if(orgaoBanco.isPresent())
            orgao.setId(orgaoBanco.get().getId());

        Optional<Setor> setorBanco = setorRepository.findByGuid(setor.getGuid());
        
        if(setorBanco.isPresent()){
            setor.setId(setorBanco.get().getId());
        }

        setor.setOrgao(orgao);

        Optional<Usuario> usuarioBanco = usuarioRepository.findBySub(papelDto.agenteSub());
        Usuario membro = new Usuario();

        if(usuarioBanco.isPresent()){
            membro = usuarioBanco.get();
        } else {
            membro.setSub(papelDto.agenteSub());
            membro.setNomeCompleto(papelDto.agenteNome());
            membro.setName(papelDto.agenteNome().split(" ")[0]);
        }
        
        membro.setPapel(papelDto.nome());

        final Usuario[] userWrapper = new Usuario[]{membro};

        List<Usuario> setoresExistente = grupo.getMembros().stream()
                                        .filter(membroGrupo -> 
                                            membroGrupo.getSub().equals(userWrapper[0].getSub())
                                        ).toList();  
                                
        if(setoresExistente.isEmpty())          
            grupo.addMembro(membro);


        this.repository.save(grupo);
        
    }

}
