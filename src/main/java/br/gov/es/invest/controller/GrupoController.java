package br.gov.es.invest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.CadastroMembroFormDto;
import br.gov.es.invest.dto.GrupoDTO;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.exception.GrupoNaoEncotradoException;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.service.OrgaoService;
import br.gov.es.invest.service.SetorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/grupo")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService service;

    private final OrgaoService orgaoService;

    private final SetorService setorService;

    @GetMapping("/")
    public GrupoDTO findById(@RequestParam String grupoId) {

        Optional<Grupo> optGrupo = service.findById(grupoId);

        if(optGrupo.isPresent()){
            return new GrupoDTO(optGrupo.get()) ;
        } else {
            throw new GrupoNaoEncotradoException(grupoId);
        }

    }

    @GetMapping("/byFilter")
    public List<GrupoDTO> findByFilter(
            @RequestParam(required = false) String nome, @RequestParam Integer pagAtual, @RequestParam Integer tamPag
        ) {
        
            return service.findAll(nome, PageRequest.of(pagAtual, tamPag)).stream().map(grupo -> new GrupoDTO(grupo)).toList();

    }

    @PutMapping("/save")
    public GrupoDTO saveGrupo(@RequestBody GrupoDTO grupoDTO) {
        //TODO: process POST request
        
        return new GrupoDTO(service.save(new Grupo(grupoDTO)));
    }

    @PutMapping("/addMembro")
    public void addMembro(@RequestBody CadastroMembroFormDto cadastroFormDto) {
        //TODO: process POST request
        Grupo grupo = service.findById(cadastroFormDto.grupo().getId()).get();
        Orgao orgao = orgaoService.findOrCreate(new Orgao(cadastroFormDto.orgao()));
        Setor setor = setorService.findOrCreate(new Setor(cadastroFormDto.setor()));


        service.addMembro(grupo, orgao, setor, cadastroFormDto.papel() );
    }
    
    @DeleteMapping("/")
    public GrupoDTO deleteGrupo(@RequestParam String idGrupo) {
        return new GrupoDTO(service.delete(idGrupo));
    }

    
}