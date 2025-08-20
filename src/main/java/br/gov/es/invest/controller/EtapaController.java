package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.EtapaDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.EtapaService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.components.FluxoConfig;
import br.gov.es.invest.utils.domains.Etapa;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/etapa")
@RequiredArgsConstructor
public class EtapaController {
    
    private final FluxoConfig fluxoConfig;
    private final EtapaService etapaService;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @GetMapping("")
    public ResponseEntity<?> getEtapa(@RequestParam(required = false) String id) {

        if(id == null) {
            List<Etapa> etapas = fluxoConfig.getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP).etapas();

            return ResponseEntity
                    .ok(etapas.stream().map(EtapaDTO::parse).toList());

        }

        return MensagemErroRest.asResponseEntity(
            HttpStatus.NOT_IMPLEMENTED, 
            "Funcionalidade não implementada ainda", 
            null
        );
    }

    @GetMapping("/doUsuario")
    public EtapaDTO getEtapaDoUsuario(@RequestParam(required = false) String userId,  @RequestHeader("Authorization") String authToken) {

        if(userId == null) {
            String sub = tokenService.validarToken(authToken.replace("Bearer ", ""));
            
            Usuario usuario = usuarioService.getUserBySub(sub).get();

            userId = usuario.getId();
        }

        return EtapaDTO.parse(fluxoConfig.getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP).etapa(etapaService.getEtapaDoUsuario(userId).getEtapaId().name()));  
               
        

    }
}
