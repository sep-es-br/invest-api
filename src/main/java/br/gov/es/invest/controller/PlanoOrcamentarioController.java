package br.gov.es.invest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.service.PlanoOrcamentarioBIService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/planoOrcamentario")
@RequiredArgsConstructor
public class PlanoOrcamentarioController {

    @Value("${frontend.host}")
    private String frontHost;

    // private final Logger logger = Logger.getLogger("PlanoOrcamentarioController");

    private final PlanoOrcamentarioService service;
    private final PlanoOrcamentarioBIService biService;

    @GetMapping("/doSigefes")
    public List<PlanoOrcamentarioDTO> getDoSigefes(@RequestParam(required = false) String codigo) {
        if(codigo == null)
            codigo = "todas";

        List<PlanoOrcamentario> planos = biService.getPlanosPorUnidade(codigo);

        return planos.stream()
            .map(plano -> new PlanoOrcamentarioDTO(plano))
            .sorted((plano1, plano2) -> plano1.codigo().compareTo(plano2.codigo()))
            .toList();
    }
    

    @GetMapping("/all")
    public ResponseEntity<List<PlanoOrcamentarioDTO>> getAllByFiltro() {

        ArrayList<PlanoOrcamentarioDTO> planosDTO = new ArrayList<>();

        service.getAllSimples().forEach(plano -> planosDTO.add(new PlanoOrcamentarioDTO(plano.getId(), plano.getCodigo(), plano.getNome())));

        return ResponseEntity.ok(planosDTO);
        

    }
    
}