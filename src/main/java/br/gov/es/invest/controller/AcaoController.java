package br.gov.es.invest.controller;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;

import br.gov.es.invest.dto.AcaoDTO;
import br.gov.es.invest.dto.ExecutarAcaoDTO;
import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.exception.SemApontamentosException;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Parecer;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.AcaoService;
import br.gov.es.invest.service.ApontamentoService;
import br.gov.es.invest.service.EtapaService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/acao")
@RequiredArgsConstructor
public class AcaoController {
    
    private final EtapaService etapaService;
    private final AcaoService acaoService;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @PostMapping("/executarAcao")
    public ResponseEntity<?> executarAcao(@RequestBody ExecutarAcaoDTO executarAcaoDTO, @RequestHeader("Authorization") String authToken) {
        
        List<Apontamento> apontamentos = null;
        Parecer parecer = null;

        Objeto objeto = Objeto.parse(executarAcaoDTO.objeto());
        if(executarAcaoDTO.parecer() != null){
            parecer = Parecer.parse(executarAcaoDTO.parecer());
        } else {
            apontamentos = executarAcaoDTO.apontamentos().stream().map(Apontamento::parse).toList();
        }
        Acao acao = Acao.parse(executarAcaoDTO.acao(), etapaService.findById(executarAcaoDTO.acao().proxEtapaId()).orElse(null)); 

        String sub = tokenService.validarToken(authToken.replace("Bearer ", ""));
            
        Usuario usuario = usuarioService.getUserBySub(sub).get();

        try {
            

            return ResponseEntity.ok(new ObjetoDto(acaoService.executarAcao(objeto, apontamentos, parecer, acao, usuario)));        
        } catch(SemApontamentosException ex){
            return MensagemErroRest.asResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                "Favor inserir algum apontamento", 
                Arrays.asList(ex.getLocalizedMessage())
            );
        }

    }
    


}
