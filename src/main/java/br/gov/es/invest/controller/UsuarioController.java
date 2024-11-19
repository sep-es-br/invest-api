package br.gov.es.invest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService service;

    @GetMapping("avatar")
    public ResponseEntity<AvatarDTO> avatarFromSub(@RequestParam String sub) {
        Usuario usuario = service.getUserWithAvatarBySub(sub);

        return ResponseEntity.ok((usuario == null || usuario.getImgPerfil() == null)  ? null : new AvatarDTO(usuario.getImgPerfil()));
    }

    @GetMapping("")
    public UsuarioDto getUsuario(@RequestParam String sub) {
        return new UsuarioDto(service.getUserBySub(sub));
    }

    @GetMapping("comAvatar")
    public UsuarioDto getUsuarioComAvatar(@RequestParam String sub) {
        return new UsuarioDto(service.getUserWithAvatarBySub(sub));
    }
    

    @PutMapping("")
    public ResponseEntity<UsuarioDto> salvarUsuario(@RequestBody UsuarioDto usuario) {
        
        Usuario user = new Usuario(usuario);
        
        user = service.save(user);

        return ResponseEntity.ok(new UsuarioDto(user));
    }
    

}
