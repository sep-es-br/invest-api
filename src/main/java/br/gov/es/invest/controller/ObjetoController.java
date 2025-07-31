package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.utils.DataListResult;


@RestController
@RequestMapping("/objeto")
@RequiredArgsConstructor
public class ObjetoController {

    private static final Logger logger = Logger.getLogger("ObjetoController");

    private final ObjetoService service;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;

    @PostMapping("/allTira")
    public ResponseEntity<?> getAllByFiltro(
        @RequestBody ObjetoFiltroDTO filtro, @RequestHeader("Authorization") String authToken
    ) {
        List<Long> idsUo = null;
        if(filtro.unidades() == null && !filtro.podeVerUnidades()) {

            authToken = authToken.replace("Bearer ", "");
    
            String sub = tokenService.validarToken(authToken);
                    
            Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
            
            List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

            idsUo = unidades.stream().map(u -> u.getId()).toList();

        } else if(filtro.unidades() != null) {
            idsUo = filtro.unidades().stream().map(UnidadeOrcamentariaDTO::id).toList();
        }

        List<Long> idsPo = filtro.planos() == null ? null : filtro.planos().stream().map(PlanoOrcamentarioDTO::id).toList();

            

        DataListResult<ObjetoTiraDTO> objetos = service.getAllListByFilter(
            filtro.exercicio(), 
            filtro.nome(), 
            idsUo, idsPo, 
            filtro.status() == null ? null : filtro.status().id(), 
            null, 
            filtro.ordem(),
            Pageable.ofSize(filtro.tamPag()).withPage(filtro.pagAtual()-1)
        );

        
        return ResponseEntity.ok(objetos);
     

    }

    
    @GetMapping("/allTiraEmProcessamento")
    public ResponseEntity<?> getAllByFiltroEmProcessamento(
        @RequestParam(required = false) String nome, @RequestParam(required = false) Long statusId,
        @RequestParam(required = false) String unidadeId, @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) String idPo, @RequestParam int pgAtual, @RequestParam int tamPag,
        @RequestParam(required = false) Long etapaId, @RequestParam boolean podeVerUnidades, @RequestHeader("Authorization") String authToken
    ) {

        try{
            List<Long> idsUo = null;
            if(unidadeId == null && !podeVerUnidades) {

                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

                idsUo = unidades.stream().map(u -> u.getId()).toList();
            } else if(unidadeId != null) {
                idsUo = new JsonMapper().readValue(unidadeId, new TypeReference<List<Long>>(){});
            }
            List<Long> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<Long>>(){});

            return ResponseEntity.ok(
                service.getAllListByFilterEmProcessamento(ano, nome, idsUo, idsPo, statusId, etapaId, null, Pageable.ofSize(tamPag).withPage(pgAtual-1))
            );

        } catch(JsonProcessingException e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao buscar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }

    }

    @GetMapping("/byId")
    public ResponseEntity<?> getById(@RequestParam Long id, @RequestParam(required = false, defaultValue="true") boolean updateStatus) {

        try{

            Optional<Objeto> optObjeto = service.getById(id, updateStatus);

            if(optObjeto.isEmpty()) {
                return MensagemErroRest.asResponseEntity(
                    HttpStatus.NOT_FOUND, 
                    "Objeto não encontrado", 
                    Arrays.asList("Objeto com id " + id + " não encontrado")
                );
            }

            Objeto objeto = optObjeto.get();
            
            if(objeto.getEmEtapa() != null){
                objeto.getEmEtapa().getEtapa().setAcoes(
                    objeto.getEmEtapa().getEtapa().getAcoes().stream().sorted((acao1, acao2) -> 
                        getAsNumberValue(acao1.getPositivo()) - getAsNumberValue(acao2.getPositivo())
                    
                    ).toList()
                );
            }
            
            return ResponseEntity.ok(new ObjetoDto(objeto));

        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

    private int getAsNumberValue(Boolean b){
        return b == null ? 0 : (b.equals(Boolean.TRUE) ? 1 : -1);
    }


    @PostMapping("/countEmProcessameto")
    public ResponseEntity<?> contarEmProcessamento(@RequestBody(required=false) ObjetoFiltroDTO filtro){

        DataListResult<ObjetoTiraDTO> objetos = service.getAllListByFilterEmProcessamento(
            filtro.exercicio(), 
            filtro.nome(), 
            filtro.unidades() == null ? null : filtro.unidades().stream().map(UnidadeOrcamentariaDTO::id).toList(), 
            filtro.planos() == null ? null : filtro.planos().stream().map(PlanoOrcamentarioDTO::id).toList(), 
            filtro.status() == null ? null : filtro.status().id(), 
            filtro.etapa() == null ? null : filtro.etapa().id(), 
            null, 
            null
            );


        return ResponseEntity.ok(objetos.ammount());


    }


    @PostMapping("")
    public ResponseEntity<ObjetoDto> cadastrarObjeto(@RequestBody ObjetoDto objetoDto, @RequestHeader("Authorization") String auth ) {
        
        Objeto objeto = new Objeto(objetoDto);
        if(objeto.getId() != null) {
            objeto.setEmEtapa(service.getById(objeto.getId()).get().getEmEtapa());
        }
        
        
        if(objeto.getResponsavel() == null) {
            auth = auth.replace("Bearer ", "");

            String sub = tokenService.validarToken(auth);

            objeto.setResponsavel( usuarioService.getUserBySub(sub).orElse(null) );
        }
        
        service.save(objeto);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteObj(Long objetoId){
        
        Optional<Objeto> optObjetoRemovido = service.getById(objetoId);

        if(optObjetoRemovido.isEmpty())
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NO_CONTENT, 
                "Objeto não encontrado", 
                null
                );

        service.findObjetoByConta(optObjetoRemovido.get().getConta());

        if( optObjetoRemovido.get().getConta().getPlanoOrcamentario() != null
         && service.findObjetoByConta(optObjetoRemovido.get().getConta()).size() == 1) {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                "Não foi possivel remover o objeto por ser o unico da despesa, uma despesa deve ter ao menos 1 objeto",
                null
            );
            
        }
        
        service.removerObjeto(objetoId);

        return ResponseEntity.ok(new ObjetoDto( optObjetoRemovido.get()));

    }
        
    
}
