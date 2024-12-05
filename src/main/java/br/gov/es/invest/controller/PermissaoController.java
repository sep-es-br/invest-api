package br.gov.es.invest.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.model.Funcao;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.service.ModuloService;
import br.gov.es.invest.service.PodeService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/permissao")
@RequiredArgsConstructor
public class PermissaoController {
    
    private final PodeService podeService;

    private final ModuloService moduloService;

    private final UsuarioService usuarioService;

    private final GrupoService grupoService;

    private final TokenService tokenService;


    @GetMapping("/grupoTemAcesso")
    public boolean checarAcesso(@RequestParam String grupoId,@RequestParam String path){
        
        return moduloService.checarAcesso(grupoId, path);
        
    }

    @GetMapping("/usuarioTemAcesso")
    public boolean checarAcessoUsuario(@RequestParam String path, @RequestHeader("Authorization") String authToken){

        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
                
        Usuario usuario = usuarioService.getUserBySub(sub);

        if(testarFuncao(usuario.getRole(), "GESTOR_MASTER")) 
            return true;
        
        return moduloService.checarAcessoUsuario(path, usuario.getId());
        
    }

    public boolean testarFuncao(Set<Funcao> funcoes, String targetFuncao){
        
        for(Funcao funcao : funcoes){
            if(funcao.getNome().equals(targetFuncao)) return true;
        }

        return false;
    }
    

}
