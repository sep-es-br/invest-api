package br.gov.es.invest.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.service.AutenticacaoService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AutenticacaoController {

    @Value("${frontend.host}")
    private String frontHost;

    private final AutenticacaoService service;

    @GetMapping("/acesso-cidadao-response")
    public ModelAndView acessoCidadaoResponse(String accessToken) {
        String tokenEmBase64 = Base64.getEncoder().encodeToString(accessToken.getBytes());
        return new ModelAndView(String.format("redirect:%s/token?token=%s", frontHost, tokenEmBase64)) ;
    }

    @GetMapping("/user-info")
    public UsuarioDto montarUsuarioDto(@RequestHeader("Authorization") String authorization) {
        return service.autenticar( authorization.replace("Bearer ", ""));
    }
}
