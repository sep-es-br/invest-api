package br.gov.es.invest.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.CadastroMembroFormDto;
import br.gov.es.invest.dto.GrupoDTO;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.exception.GrupoNaoEncotradoException;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.service.OrgaoService;
import br.gov.es.invest.service.SetorService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/grupo")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService service;

    private final OrgaoService orgaoService;

    private final SetorService setorService;

    @GetMapping("")
    public ResponseEntity<?> findById(@RequestParam(required = false) Long grupoId, @RequestParam(required = false) String nome) {

        if(grupoId != null) {

            Optional<Grupo> optGrupo = service.findById(grupoId);

            if(optGrupo.isPresent()){
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GrupoDTO.parse(optGrupo.get()));
            } else {
                GrupoNaoEncotradoException ex = new GrupoNaoEncotradoException(grupoId);
    
                return MensagemErroRest.asResponseEntity(
                    HttpStatus.NO_CONTENT, 
                    ex.getLocalizedMessage(), 
                    Collections.singletonList(ex.getLocalizedMessage())
                );
            }
        } else {

            List<GrupoDTO> dtoList = service.findAll(nome).stream()
                                        .map(GrupoDTO::parse)
                                        .toList();

            return ResponseEntity
                    .ok()
                    .body(dtoList);

        }


    }

    @GetMapping("/membros")
    public ResponseEntity<?> getMembros(@RequestParam String grupoId){
        return ResponseEntity.ok(service.getListaMembros(grupoId));
    }

    @GetMapping("/quantidadeMembros")
    public int getMethodName(@RequestParam Long grupoId) {
        return service.quantidadeDeMembros(grupoId);
    }
    

    @GetMapping("/byFilter")
    public List<GrupoDTO> findByFilter(
            @RequestParam(required = false) String nome, @RequestParam Integer pagAtual, @RequestParam Integer tamPag
        ) {
        
            return service.findAll(nome, Pageable.ofSize(tamPag).withPage(pagAtual)).stream().map(GrupoDTO::parse).toList();

    }
    

    @GetMapping("/byUsuario")
    public List<GrupoDTO> findByUsuario(
            @RequestParam Long usuarioId
        ) {
        
            return service.getGruposDoUsuario(usuarioId).stream().map(GrupoDTO::parse).toList();

    }

    @PutMapping("/save")
    public GrupoDTO saveGrupo(@RequestBody GrupoDTO grupoDTO) {
        
        return GrupoDTO.parse(service.save(new Grupo(grupoDTO)));
    }

    @PutMapping("/addMembro")
    public GrupoDTO addMembro(@RequestBody CadastroMembroFormDto cadastroFormDto) {
        Grupo grupo = service.findById(cadastroFormDto.grupo().id()).get();
        Orgao orgao = orgaoService.findOrCreateByGuidOrSigla(new Orgao(cadastroFormDto.orgao()));
        Setor setor = cadastroFormDto.setor() == null ? null : setorService.findOrCreate(new Setor(cadastroFormDto.setor()), orgao);

        service.addMembro(grupo, orgao, setor, cadastroFormDto.papel());

        grupo = service.findById(grupo.getId()).get();

        return GrupoDTO.parse(grupo);
    }
    
    @DeleteMapping("/")
    public GrupoDTO deleteGrupo(@RequestParam Long idGrupo) {
        return GrupoDTO.parse(service.delete(idGrupo));
    }
    
    @DeleteMapping("/membro")
    public GrupoDTO deleteGrupo(@RequestParam Long idGrupo, @RequestParam Long idMembro) {
        return GrupoDTO.parse(service.removerMembro(idGrupo, idMembro));
    }

    
}