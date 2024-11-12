package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.CustoDTO;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.service.CustoService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/custo")
@RequiredArgsConstructor
public class CustoController {

    @Value("${frontend.host}")
    private String frontHost;

    private final CustoService service;

    @GetMapping("/all")
    public ResponseEntity<List<CustoDTO>> getAllByExercicio(@RequestParam String exercicio) {
        List<CustoDTO> custos = service.getAllByExercicio(exercicio).stream().map((custo) -> new CustoDTO(custo)).toList();
        
        return ResponseEntity.ok(custos);
    }
    

    @GetMapping("/totalPrevisto")
    public ResponseEntity<Double> getTotalPrevisto(@RequestParam String exercicio) {
        double totalPrevisto = service.totalPrevisto(exercicio);

        return ResponseEntity.ok(totalPrevisto);
    }
    
    @GetMapping("/totalHomologado")
    public ResponseEntity<Double> getTotalHomologado(@RequestParam String exercicio) {
        double totalHomologado = service.totalHomologado(exercicio);

        return ResponseEntity.ok(totalHomologado);
    }
    
    @GetMapping("/totalAutorizado")
    public ResponseEntity<Double> getTotalAutorizado() {
        double totalAutorizado = service.totalAutorizado();

        return ResponseEntity.ok(totalAutorizado);
    }    
    
    @GetMapping("/totalDisponivel")
    public ResponseEntity<Double> getTotalDisponivel() {
        double totalDisponivel = service.totalDisponivel();

        return ResponseEntity.ok(totalDisponivel);
    }

    
}
