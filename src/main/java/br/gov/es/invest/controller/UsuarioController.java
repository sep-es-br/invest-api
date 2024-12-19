package br.gov.es.invest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;



@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService service;
    private final TokenService tokenService;

    @GetMapping("avatar")
    public AvatarDTO avatarFromSub(@RequestParam(required = false) String sub, @RequestHeader("Authorization") String authToken) {

        authToken = authToken.replace("Bearer ", "");
        
        sub = sub == null ? tokenService.validarToken(authToken) : sub;
                
        Optional<Usuario> optUsuario = service.getUserBySub(sub);

        return (optUsuario.isEmpty() || optUsuario.get().getImgPerfil() == null)  ? null : new AvatarDTO(optUsuario.get().getImgPerfil());
    }

    @GetMapping("")
    public UsuarioDto getUsuario(@RequestParam(required = false) String sub, @RequestHeader("Authorization") String authToken) {
        authToken = authToken.replace("Bearer ", "");
        
        
        sub = sub == null ? tokenService.validarToken(authToken) : sub;

        Optional<Usuario> optUsuario = service.getUserBySub(sub);

        return optUsuario.isPresent() ? new UsuarioDto(optUsuario.get()) : null;
    }

    @GetMapping("/byGrupo")
    public List<UsuarioDto> getUsuarioByGrupo(@RequestParam String grupoId) {

        return service.findByGrupo(grupoId).stream().map(usuario -> new UsuarioDto(usuario)).toList();
    }

    @GetMapping("comAvatar")
    public UsuarioDto getUsuarioComAvatar(@RequestParam(required = false) String sub, @RequestHeader("Authorization") String authToken) {
        authToken = authToken.replace("Bearer ", "");
        
        sub = sub == null ? tokenService.validarToken(authToken) : sub;

        Optional<Usuario> optUsuario = service.getUserBySub(sub);

        return optUsuario.isPresent() ? new UsuarioDto(optUsuario.get()) : null;
    }
    

    @PutMapping("")
    public ResponseEntity<UsuarioDto> salvarUsuario(@RequestBody UsuarioDto usuario) {
        
        Usuario user = new Usuario(usuario);
        
        user = service.save(user);

        return ResponseEntity.ok(new UsuarioDto(user));
    }
    

}
