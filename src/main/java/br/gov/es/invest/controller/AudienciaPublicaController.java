/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.controller;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.AreaTematica;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.AreaTematicaService;
import br.gov.es.invest.service.AudienciaPublicaService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DataListResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gean.carneiro
 */
@RestController
@RequestMapping("/audiencia-publica")
@RequiredArgsConstructor
public class AudienciaPublicaController {
    
    private final AudienciaPublicaService audienciaPublicaSrv;
    private final AreaTematicaService areaTematicaSrv;
    
    
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    
    @GetMapping("/listaPropostas")
    public ResponseEntity<?> getListaAudiencia(
        @RequestParam(required = false) String unidadeIds,
        @RequestParam(required = false) String areaTematicaId,
        @RequestParam(required = false, defaultValue = "") String filtroTexto,
        @RequestParam Boolean podeVerUnidades, @RequestHeader("Authorization") String authToken   
    ) {
        
        List<String> idsUo = Arrays.asList();
        if(unidadeIds == null && !podeVerUnidades) {

            authToken = authToken.replace("Bearer ", "");

            String sub = tokenService.validarToken(authToken);

            Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);


            List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByOrgaoId(usuario.getSetor().getOrgao());

            idsUo = unidades.stream().map(u -> u.getId()).toList();
        } else if(unidadeIds != null) {
            idsUo = Arrays.asList(unidadeIds.split(";"));
        }
        
        List<String> codsUo = unidadeOrcamentariaService.getCodsByIds(idsUo);
        
        
        String areaTematicaNome = Optional.ofNullable(areaTematicaId)
                                    .flatMap(this.areaTematicaSrv::findById)
                                    .map(AreaTematica::getNome)
                                    .orElse(null);
        

        return ResponseEntity.ok(new DataListResult<>(audienciaPublicaSrv.listaAudienciaPublica(
                codsUo,
                areaTematicaNome,
                filtroTexto
        )));
            

    }
    
    @GetMapping("/idUltimaAudiencia")
    public ResponseEntity<?> getUltimaAudiencia() {
        return ResponseEntity.ok(Map.of("id", audienciaPublicaSrv.idUltimaAudiencia())) ;
    }
    
}
