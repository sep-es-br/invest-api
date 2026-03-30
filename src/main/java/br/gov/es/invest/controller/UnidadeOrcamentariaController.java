package br.gov.es.invest.controller;

import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.projection.UnidadeOrcamentariaDTOProjection;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaBIService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeOrcamentariaController {
    
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final UnidadeOrcamentariaService service;
    private final UnidadeOrcamentariaBIService biService;

    @GetMapping("/all")
    public ResponseEntity<List<UnidadeOrcamentariaDTO>> getAllByFiltro(
            @RequestParam(required = false, defaultValue = "") String version
    ) {

        ArrayList<UnidadeOrcamentariaDTO> unidadesDTO = new ArrayList<>();
        
        List<UnidadeOrcamentariaDTOProjection> result;
        
        switch (version) {
            case "fluxo" -> result = service.getAllSimplesForFluxo();
            default -> result = service.getAllSimples();
        }
        
//        for(UnidadeOrcamentariaDTOProjection unidade: result) {;
//            if(!unidade.codigo().startsWith("0") && !unidade.codigo().startsWith("8"))
//                unidadesDTO.add(new UnidadeOrcamentariaDTO(unidade.id(), unidade.guid(), unidade.codigo(), unidade.nome(), unidade.sigla()));
//        }

        return ResponseEntity.ok(result.stream().map(unidade -> new UnidadeOrcamentariaDTO(unidade.id(), unidade.guid(), unidade.codigo(), unidade.nome(), unidade.sigla())).toList());
        

    }

    @GetMapping("/doSigefes")
    public ResponseEntity<List<UnidadeOrcamentariaDTO>> getAllDoSigefes() {

        List<UnidadeOrcamentaria> unidades = biService.getTodasUnidades(null);
        
        return ResponseEntity.ok(unidades.stream().map(
            unidade -> new UnidadeOrcamentariaDTO(unidade)
        ).sorted((unidade1, unidade2) -> unidade1.codigo().compareTo(unidade2.codigo())).toList());
        

    }

    @GetMapping("/doUsuario")
    public List<UnidadeOrcamentariaDTO> findBySigla(@RequestHeader("Authorization") String authToken) {
        authToken = authToken.replace("Bearer ", "");
        
        String sub = tokenService.validarToken(authToken);
                
        Agente usuario = usuarioService.getUserBySub(sub).orElse(null);
        
        List<UnidadeOrcamentaria> unidades = this.service.findByAgente(usuario.getId(), false);

        return unidades.stream().map(UnidadeOrcamentariaDTO::new).toList();


    }
    
    @GetMapping("byCodigo/{codigo}")
    public ResponseEntity<UnidadeOrcamentariaDTO> getByCodigo(
            @PathVariable String codigo
    ){
                
        return ResponseEntity.of(this.service.getByCod(codigo).map(UnidadeOrcamentariaDTO::new));
    }
    
}