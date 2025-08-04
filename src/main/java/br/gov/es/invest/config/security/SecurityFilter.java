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
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Funcao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.ACService;
import br.gov.es.invest.service.ModuloService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final ModuloService moduloService;
    private final ACService acSrv;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        if (checarWhiteList(request, Arrays.asList(
            "/user-info",
            "/oauth2/authorization",
            "/acesso-cidadao-response",
            "acesso-cidadao-response.html",
            "importarPentaho", "teste"
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

                Usuario user = usuarioService.getUserBySub(sub).orElse(null);
                
                Set<Funcao> funcoes = user.getRole();
                
                String acToken = acSrv.getClientToken();
                
                List<Papel> papeisAtualizados = acSrv.getPapeisBySub(sub, acSrv.getClientToken()).stream()
                            .map(papel -> acSrv.gerarPapelFromRespSemSalvar(papel, acToken))
                            .collect(Collectors.toList());
                       
                if(!Funcao.testarFuncao(funcoes, "GESTOR_MASTER")
                && !checarAcesso(request, papeisAtualizados)) {
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
                if (LocalDateTime.now().isAfter(expiresAt))
                    erros.add("Token expirado em " + expiresAt);

                enviarMensagemTokenInvalido(erros, response, HttpStatus.UNAUTHORIZED);

                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    
    private boolean checarAcesso(HttpServletRequest request, List<Papel> papeis){
        String url = request.getHeader("Origin-URL");

        if(url == null) return false;

        String[] paths = url.split("/");

        for(int i = 0; i < paths.length-1; i++){
            String pathId = paths[i] + paths[i+1];
            
            if(!moduloService.checarAcessoUsuario(pathId, papeis))
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
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(objetoErro.codigo());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), objetoErro);
    }
}
