package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.FluxoDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.service.FluxoService;
import br.gov.es.invest.service.GrupoService;
import br.gov.es.invest.utils.domains.Fluxo;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fluxo")
@RequiredArgsConstructor
public class FluxoController {
    
    private final FluxoService fluxoService;
    private final GrupoService grupoSrv;
    

    @GetMapping("")
    public ResponseEntity<?> find(@RequestParam(required = false) String id) {

        if(id == null) {
            List<FluxoDTO> fluxoDTOs = fluxoService.findAll().stream()
                                    .map(fluxo -> new FluxoDTO(fluxo, grupoSrv))
                                    .toList();

            return ResponseEntity.ok(fluxoDTOs);
        }

        Fluxo fluxo = fluxoService.findByFluxoId(id);

        if(fluxo == null) {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NOT_FOUND, 
                "Fluxo Inexistente", 
                null
            );
        }

        return ResponseEntity.ok(FluxoDTO.parse(fluxo, grupoSrv));
    }

    @GetMapping("/withEtapa")
    public ResponseEntity<?> findWithEtapa(@RequestParam String etapaId){
        Fluxo fluxo = fluxoService.findWithEtapa(etapaId);

        if(fluxo == null) {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NOT_FOUND, 
                "Não foi possivel localizar fluxo que possui essa etapa definida", 
                List.of("Id buscado: " + etapaId)
            );
        }

        return ResponseEntity.ok(FluxoDTO.parse(fluxo, grupoSrv));


    }

    

}
