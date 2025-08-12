package br.gov.es.invest.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
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
        
        sub = Optional.ofNullable(sub).orElse(tokenService.validarToken(authToken));

        Optional<Usuario> optUsuario = service.getUserBySub(sub);

        return UsuarioDto.parse(optUsuario.orElse(null));
    }

    @GetMapping("comAvatar")
    public UsuarioDto getUsuarioComAvatar(@RequestParam(required = false) String sub, @RequestHeader("Authorization") String authToken) {
        authToken = authToken.replace("Bearer ", "");
        
        sub = Optional.ofNullable(sub).orElse(tokenService.validarToken(authToken));

        Optional<Usuario> optUsuario = service.getUserBySub(sub);

        return UsuarioDto.parse( optUsuario.orElse(null) );
    }
    

    @PutMapping("")
    public ResponseEntity<UsuarioDto> salvarUsuario(@RequestBody UsuarioDto usuario) {
        
        return ResponseEntity.ok(UsuarioDto.parse(service.save(usuario)));
    }
    

}
