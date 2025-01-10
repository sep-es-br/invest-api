package br.gov.es.invest.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaBIService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeOrcamentariaController {

    @Value("${frontend.host}")
    private String frontHost;

    
    private final TokenService tokenService;
    private final UsuarioService usuarioService;


    // private final Logger logger = Logger.getLogger("PlanoOrcamentarioController");

    private final UnidadeOrcamentariaService service;
    private final UnidadeOrcamentariaBIService biService;

    @GetMapping("/all")
    public ResponseEntity<List<UnidadeOrcamentariaDTO>> getAllByFiltro() {

        ArrayList<UnidadeOrcamentariaDTO> unidadesDTO = new ArrayList<>();
        
        for(UnidadeOrcamentariaDTOProjection unidade: service.getAllSimples()) {
            unidadesDTO.add(new UnidadeOrcamentariaDTO(unidade.id(), unidade.guid(), unidade.codigo(), unidade.nome(), unidade.sigla()));
        }

        return ResponseEntity.ok(unidadesDTO);
        

    }

    @GetMapping("/doSigefes")
    public ResponseEntity<List<UnidadeOrcamentariaDTO>> getAllDoSigefes() {

        List<UnidadeOrcamentaria> unidades = biService.getTodasUnidades();
        
        return ResponseEntity.ok(unidades.stream().map(
            unidade -> new UnidadeOrcamentariaDTO(unidade)
        ).sorted((unidade1, unidade2) -> unidade1.codigo().compareTo(unidade2.codigo())).toList());
        

    }

    @GetMapping("/doUsuario")
    public UnidadeOrcamentariaDTO findBySigla(@RequestHeader("Authorization") String authToken) {
        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
                
        Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);

        if(usuario.getSetor() == null) {
            return null;
        } else {

            UnidadeOrcamentaria unidade = this.service.findBySigla(usuario.getSetor().getOrgao().getSigla());

            return unidade == null ? null : new UnidadeOrcamentariaDTO(unidade);

        }

    }
    
}