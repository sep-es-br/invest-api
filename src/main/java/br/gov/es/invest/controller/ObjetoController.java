package br.gov.es.invest.controller;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;

import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.dto.StatusDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.dto.ContaDto;
import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.StatusService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/objeto")
@RequiredArgsConstructor
public class ObjetoController {

    @Value("${frontend.host}")
    private String frontHost;

    private final Logger logger = Logger.getLogger("ObjetoController");

    private final ObjetoService service;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;

    @GetMapping("/allTira")
    public ResponseEntity<?> getAllByFiltro(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String statusId,
        @RequestParam(required = false) String unidadeId, @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) String idPo, @RequestParam int pgAtual, @RequestParam int tamPag,
        @RequestParam boolean podeVerUnidades, @RequestHeader("Authorization") String authToken
    ) {

        try{
            List<String> idsUo = null;
            if(unidadeId == null && !podeVerUnidades) {

                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

                idsUo = unidades.stream().map(u -> u.getId()).toList();
            } else if(unidadeId != null) {
                idsUo = new JsonMapper().readValue(unidadeId, new TypeReference<List<String>>(){});
            }

            List<String> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<String>>(){});

            List<Objeto> objetos = service.getAllListByFilter(ano, nome, idsUo, idsPo, statusId, null, PageRequest.of(pgAtual-1, tamPag));

            List<ObjetoTiraDTO> objetosDTO = objetos.stream().map(obj -> {                
                return new ObjetoTiraDTO(obj);
            }).toList();

            return ResponseEntity.ok(objetosDTO);
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao buscar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }

    }

    
    @GetMapping("/allTiraEmProcessamento")
    public ResponseEntity<?> getAllByFiltroEmProcessamento(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String statusId,
        @RequestParam(required = false) String unidadeId, @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) String idPo, @RequestParam int pgAtual, @RequestParam int tamPag,
        @RequestParam(required = false) String etapaId, @RequestParam boolean podeVerUnidades, @RequestHeader("Authorization") String authToken
    ) {

        try{
            List<String> idsUo = null;
            if(unidadeId == null && !podeVerUnidades) {

                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

                idsUo = unidades.stream().map(u -> u.getId()).toList();
            } else if(unidadeId != null) {
                idsUo = new JsonMapper().readValue(unidadeId, new TypeReference<List<String>>(){});
            }
            List<String> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<String>>(){});

            List<Objeto> objetos = service.getAllListByFilterEmProcessamento(ano, nome, idsUo, idsPo, statusId, etapaId, null, PageRequest.of(pgAtual-1, tamPag));

            List<ObjetoTiraDTO> objetosDTO = objetos.stream().map(obj -> {                
                return new ObjetoTiraDTO(obj);
            }).toList();

            return ResponseEntity.ok(objetosDTO);
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao buscar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }

    }

    @GetMapping("/byId")
    public ResponseEntity<?> getById(@RequestParam String id, @RequestParam(required = false) boolean updateStatus) {

        try{

            Optional<Objeto> optObjeto = service.getById(id, true);

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

    @GetMapping("/byFiltro")
    public ResponseEntity<?> findByFiltro(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String statusId,
        @RequestParam(required = false) String unidadeId, @RequestParam(required = false) Integer ano,
        @RequestParam(required = false) String planoId
    ){
        List<Objeto> objList = service.findByFilter(nome, unidadeId, planoId, ano, null);

        List<ObjetoTiraDTO> objDto = objList.stream().map(obj -> new ObjetoTiraDTO(obj)).toList();

        return ResponseEntity
                .ok()
                .body(objDto);
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
    public ResponseEntity<?> deleteObj(String objetoId){
        
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


    @GetMapping("/statusCadastrado")
    public List<StatusDTO> findStatusCadastrados() {
        return service.findStatusCadastrados().stream().map(StatusDTO::parse).toList();
    }
    
    

    @GetMapping("/countInvestimentoFiltro")
    public ResponseEntity<?> getAmmoutByInvestimentoFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam Integer exercicio
    ) {
        try{

            List<String> idsUo = codUnidade == null ? null : new JsonMapper().readValue(codUnidade, new TypeReference<List<String>>() {});
            List<String> idsPo = codPO == null ? null : new JsonMapper().readValue(codPO, new TypeReference<List<String>>() {});

            return ResponseEntity.ok(service.countByInvestimentoFilter(nome, idsUo, idsPo, exercicio));
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao contar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }

    }

    @GetMapping("/count")
    public ResponseEntity<?> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String unidadeId,
        @RequestParam Integer ano, @RequestParam(required = false) String idPo, @RequestParam(required = false) String statusId, 
        @RequestParam boolean podeVerUnidades, @RequestHeader("Authorization") String authToken
    ) {
        try{
            
            List<String> idsUo = null;
            if(unidadeId == null && !podeVerUnidades) {

                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

                idsUo = unidades.stream().map(u -> u.getId()).toList();
            } else if(unidadeId != null) {
                idsUo = new JsonMapper().readValue(unidadeId, new TypeReference<List<String>>(){});
            }
            List<String> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<String>>(){});

            List<Objeto> objetos = service.getAllListByFilter(ano, nome, idsUo, idsPo, statusId, null, null);

            return ResponseEntity.ok(objetos.size());
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao contar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }
        

    }

    

    @GetMapping("/countEmProcessameto")
    public ResponseEntity<?> getAmmoutByFilterEmProcessamento(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String unidadeId, @RequestParam(required = false) String etapaId,
        @RequestParam Integer ano, @RequestParam(required = false) String idPo, @RequestParam(required = false) String statusId,
        @RequestParam boolean podeVerUnidades, @RequestHeader("Authorization") String authToken
    ) {
        try{
            
            List<String> idsUo = null;
            if(unidadeId == null && !podeVerUnidades) {

                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

                idsUo = unidades.stream().map(u -> u.getId()).toList();
            } else if(unidadeId != null) {
                idsUo = new JsonMapper().readValue(unidadeId, new TypeReference<List<String>>(){});
            }
            List<String> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<String>>(){});


            List<Objeto> objetos = service.getAllListByFilterEmProcessamento(ano, nome, idsUo, idsPo, statusId, etapaId, null, null);

            return ResponseEntity.ok(objetos.size());
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return MensagemErroRest.asResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro desconhecido ao contar objetos", 
                Collections.singletonList(e.getLocalizedMessage())
            );
        }
        

    }
    
}
