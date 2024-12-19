package br.gov.es.invest.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.ModuloDto;
import br.gov.es.invest.dto.PodeDto;
import br.gov.es.invest.dto.ItemMenu;
import br.gov.es.invest.model.Funcao;
import br.gov.es.invest.model.Grupo;
import br.gov.es.invest.model.Modulo;
import br.gov.es.invest.model.Pode;
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

    @PutMapping("/acessoTeste")
    public void testeDeAcesso(@RequestBody Map<String, String> map) {

        for(Entry<String, String> entry : map.entrySet()){
            Logger.getGlobal().info(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println();

    }
    


    @GetMapping("/grupoTemAcesso")
    public boolean checarAcesso(@RequestParam String grupoId,@RequestParam String path){
        
        return moduloService.checarAcesso(grupoId, path);
        
    }

    @GetMapping("/usuarioTemAcesso")
    public boolean checarAcessoUsuario(@RequestParam String path, @RequestHeader("Authorization") String authToken){

        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
                
        Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);

        if(testarFuncao(usuario.getRole(), "GESTOR_MASTER")) 
            return true;
        
        return moduloService.checarAcessoUsuario(path, usuario.getId());
        
    }

    @GetMapping("/byModuloGrupo")
    public PodeDto getByModuloGrupo(@RequestParam String idModulo, @RequestParam String idGrupo) {


        Pode pode = podeService.findByGrupoModulo(idModulo, idGrupo);

        return pode == null ? null : new PodeDto(pode);
    }

    @GetMapping("")
    public PodeDto getPermissao(@RequestParam String path, @RequestHeader("Authorization") String authToken){
    
        Modulo modulo = moduloService.findByPathId(path);

        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
        
        Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
        if(testarFuncao(usuario.getRole(), "GESTOR_MASTER")) {
            return new PodeDto(
                null, 
                modulo == null ? null : new ModuloDto(modulo), 
                true, 
                true, 
                true, 
                true, 
                true
            );
        }

        if(modulo == null) return null;

        ArrayList<Pode> permissoes = new ArrayList<>();
        
        for(Grupo grupo : grupoService.getGruposDoUsuario(usuario.getId())){
            Pode pode = podeService.findByGrupoModulo(modulo.getId(), grupo.getId());
            if(pode != null) {
                permissoes.add(pode);
            }
        }

        if(permissoes.isEmpty()) return null;

        Pode podeMax = gerarPermissaoMaxima(permissoes);
        
        return new PodeDto(podeMax);
        
    }

    @GetMapping("/buildMenu")
    public List<ItemMenu> buildMenu(@RequestHeader("Authorization") String authToken){
    
        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
        
        Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);

        boolean isGestorMaster = testarFuncao(usuario.getRole(), "GESTOR_MASTER");
        
        // boolean isGestorMaster = false;

        return Arrays.asList(new ItemMenu(
            "Inventário", 
            "home", 
            isGestorMaster || moduloService.checarAcessoUsuario("inventario", usuario.getId()), 
            "/inventario", 
            Arrays.asList(new ItemMenu(
                "Investimentos", 
                null, 
                isGestorMaster || moduloService.checarAcessoUsuario("inventarioinvestimentos", usuario.getId()), 
                "/investimentos", 
                null
            ))
        ), new ItemMenu(
            "Minha Carteira", 
            "archive", 
            isGestorMaster || moduloService.checarAcessoUsuario("carteira", usuario.getId()), 
            "/carteira", 
            Arrays.asList( new ItemMenu(
                "Investimentos", 
                null, 
                isGestorMaster || moduloService.checarAcessoUsuario("carteirainvestimentos", usuario.getId()), 
                "/investimentos", 
                null
            ), new ItemMenu(
                "Objetos", 
                null, 
                isGestorMaster || moduloService.checarAcessoUsuario("carteiraobjetos", usuario.getId()), 
                "/objetos", 
                null
            )

            )
        ), new ItemMenu(
            null ,
            "graph", 
            false, 
            null, 
            null

        ));
        
    }

    private Pode gerarPermissaoMaxima(List<Pode> permissoes){
        Pode out = new Pode();
        out.setModulo(permissoes.get(0).getModulo());
        for (Pode pode : permissoes){
            out.setListar(out.isListar() || pode.isListar());
            out.setVisualizar(out.isVisualizar() || pode.isVisualizar());
            out.setCriar(out.isCriar() || pode.isCriar());
            out.setEditar(out.isEditar() || pode.isEditar());
            out.setExcluir(out.isExcluir() || pode.isExcluir());
        }
        return out;
    }

    private boolean testarFuncao(Set<Funcao> funcoes, String targetFuncao){
        
        for(Funcao funcao : funcoes){
            if(funcao.getNome().equals(targetFuncao)) return true;
        }

        return false;
    }

    
    

}
