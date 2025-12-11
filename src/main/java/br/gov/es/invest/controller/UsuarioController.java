package br.gov.es.invest.controller;

import br.gov.es.invest.dto.AvatarDTO;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.usuario.SalvarUsuarioForm;
import br.gov.es.invest.factory.UsuarioFactory;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DataListResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioFactory usuarioFactory;
    
    private final UsuarioService service;
    private final TokenService tokenService;

    @GetMapping("avatar")
    public AvatarDTO avatarFromSub(@RequestParam(required = false) String sub, @RequestHeader("Authorization") String authToken) {

        authToken = authToken.replace("Bearer ", "");
        
        sub = sub == null ? tokenService.validarToken(authToken) : sub;
                
        Optional<Agente> optUsuario = service.getUserBySub(sub);

        return (optUsuario.isEmpty() || optUsuario.get().getImgPerfil() == null)  ? null : new AvatarDTO(optUsuario.get().getImgPerfil());
    }

    @GetMapping({"", "/{userId}"})
    public ResponseEntity<?> getUsuario(
            @PathVariable Optional<Long> userId,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestHeader("Authorization") String authToken
    ) {
        
        if(userId.isPresent()){
            
            Optional<Agente> optUsuario;
            
            if(userId.get() == -1) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);

                optUsuario = service.getUserBySub(sub);
                
            } else {
                optUsuario = service.findById(userId.get());
            }

            return ResponseEntity.ok(optUsuario.map(this.usuarioFactory::toUsuarioDto).orElseThrow());
        } else {
            Assert.notNull(pageNumber, "Indique o numero da pagina");
            Assert.notNull(pageSize, "Indique o tamanho da pagina");
            
            Page<Agente> usuarios = service.findAllPaged(term, PageRequest.of(pageNumber, pageSize));
            
            return ResponseEntity.ok(new DataListResult<>(usuarios.map(this.usuarioFactory::toUsuarioResponse)));
        }
    }

    @PutMapping("")
    public ResponseEntity<UsuarioDto> salvarUsuario(@RequestBody SalvarUsuarioForm usuario) {
        
        return ResponseEntity.ok(UsuarioDto.parse(service.save(usuario)));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDto> removerAgente(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(UsuarioDto.parse(service.removerAgente(id)));
    }
    

}
