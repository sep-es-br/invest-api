package br.gov.es.invest.config.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Funcao;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.ModuloService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final ModuloService moduloService;
    
    @Value("${papel.geral}")
    private String papelGeral;
    
    @Value("${papel.capitacao}")
    private String papelCapitacao;
    
    @Value("${papel.indicadores}")
    private String papelIndicadores;
    
    @Value("${papel.indicadoresAdmin}")
    private String papelIndicadoresAdmin;
    
    @Value("${papel.sigefes}")
    private String papelSigefes;
    
    @Value("${papel.projEstrategico}")
    private String papelProjEstrategico;
    
    @Value("${papel.gestaoFiscal}")
    private String papelGestaoFiscal;
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (checarWhiteList(request, Arrays.asList(
            "/user-info",
            "/oauth2/authorization",
            "/acesso-cidadao-response",
            "acesso-cidadao-response.html",
            "importarPentaho" 
        ))) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recuperarToken(request);
        if(token == null) {
            
            MensagemErroRest erro = new MensagemErroRest(
                HttpStatus.UNAUTHORIZED,
                "Usuario não autenticado", 
                Collections.singletonList("Usuario não autenticado")
            );
            enviarMensagemErro(erro, response);
            return;
        } else {
            try {
                String sub = tokenService.validarToken(token);

                Usuario user = usuarioService.getUserBySub("c8c5831f-c57e-4a5c-bc91-0f3dc5da4d2b");
                // Usuario user = usuarioService.getUserBySub(sub);
                
                if(user == null) {
                    MensagemErroRest erro = new MensagemErroRest(
                        HttpStatus.FORBIDDEN,
                        "Usuário não existe", 
                        Arrays.asList("Usuário não existe", "Favor incluir o usuario em algum grupo")
                    );
                    enviarMensagemErro(erro, response);
                    return;
                }

                Set<Funcao> funcoes = user.getRole();
                
                if(!Funcao.testarFuncao(funcoes, "GESTOR_MASTER")
                && !checarAcesso(request, user.getId())) {
                    MensagemErroRest erro = new MensagemErroRest(
                        HttpStatus.FORBIDDEN,
                        "Usuário sem permissão", 
                        Arrays.asList("Usuário sem permissão", "Favor incluir o usuario em algum grupo")
                    );
                    enviarMensagemErro(erro, response);
                    return;
                }
                
                List<SimpleGrantedAuthority> authorities = funcoes.stream().map(funcao -> new SimpleGrantedAuthority(funcao.getNome())).toList();
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        sub, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JWTVerificationException e) {
                var expiresAt = LocalDateTime.ofInstant(JWT.decode(token).getExpiresAt().toInstant(), ZoneOffset.of("-03:00"));
                List<String> erros = new ArrayList<>();
                erros.add("Por favor, faça o login novamente.");
                if (LocalDateTime.now().isAfter((ChronoLocalDateTime<?>) expiresAt))
                    erros.add("Token expirado em " + expiresAt);
                    enviarMensagemTokenInvalido(erros, response, HttpStatus.FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checarAcesso(HttpServletRequest request, String userId){
        String url = request.getHeader("Origin-URL");

        if(url == null) return false;

        String[] paths = url.split("/");

        for(int i = 0; i < paths.length-1; i++){
            String pathId = paths[i] + paths[i+1];
            
            if(!moduloService.checarAcessoUsuario(pathId, userId))
                return false;
        }

        return true;
    }

    private boolean checarWhiteList(HttpServletRequest request, List<String> whitelist){
        
        for(String endereco : whitelist) {
            if(request.getRequestURI().endsWith(endereco)){
                return true;
            }
        }

        return false;
    }

    private boolean checarPermissao(String permissoes, List<String> roles) {
        for(String permissao : permissoes.split(",")) {
            if(roles.contains(permissao.trim())) return true;
        }
        return false;
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
    
    private void enviarMensagemTokenInvalido(List<String> erros, HttpServletResponse response, HttpStatus status) throws IOException {
        MensagemErroRest mensagem = new MensagemErroRest(status, "Token Inválido", erros);
        enviarMensagemErro(mensagem, response);
    }
    
    private void enviarMensagemErro(MensagemErroRest objetoErro, HttpServletResponse response) throws IOException {
        String mensagem = ToStringBuilder.reflectionToString(objetoErro, ToStringStyle.JSON_STYLE);
        response.setHeader("Content-Type", "application/json");
        response.setStatus(objetoErro.codigo());
        response.getWriter().write(mensagem);
    }
}
