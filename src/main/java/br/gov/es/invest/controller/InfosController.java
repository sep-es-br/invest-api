package br.gov.es.invest.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.dto.CardsTotaisDto;
import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.SetorDto;
import br.gov.es.invest.dto.ValoresCusto;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.ACService;
import br.gov.es.invest.service.AnoService;
import br.gov.es.invest.service.CustoService;
import br.gov.es.invest.service.InfosService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import lombok.RequiredArgsConstructor;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/infos")
@RequiredArgsConstructor
public class InfosController {

    @Value("${frontend.host}")
    private String frontHost;

    private final InvestimentosBIService investimentosBIService;

    private final InfosService service;
    private final AnoService anoService;
    private final ACService aCService;
    private final CustoService custoService;
    private final UnidadeOrcamentariaService unidadeService;
    private final PlanoOrcamentarioService planoService;

    @GetMapping("/allAnos")
    public ResponseEntity<Set<Integer>> getTodosAnos() {

        return ResponseEntity.ok(anoService.getAllAnos());

    }

    @GetMapping("/iconesDisponiveis")
    public List<String> getIconesDisponiveis() {
        
       return service.getIconesDisponiveis();
    }

    @GetMapping("/unidades")
    public List<OrgaoDto> getUnidades() {
        return aCService.getOrgaos().stream().map(orgao -> new OrgaoDto(orgao)).toList();
    }
    
    @GetMapping("/setores")
    public List<SetorDto> getSetores(@RequestParam String unidadeGuid) {
        return aCService.getSetores(unidadeGuid);
    }
    
    @GetMapping("/papeis")
    public List<PapelDto> getPapeis(@RequestParam String setorGuid) {
        return aCService.getPapeis(setorGuid);
    }

    @GetMapping("/cardsTotais")
    public CardsTotaisDto getCardsTotais(
        @RequestParam(required=false) String nome,
        @RequestParam(required=false) String idUo, @RequestParam(required=false) String idFonte,
        @RequestParam(required=false) String idPo, @RequestParam Integer ano
        ) {

            ValoresCusto totaisCusto = custoService.getValoresTotais(
                    nome,
                    idFonte,
                    ano,
                    idUo, 
                    idPo
                );

            String codUo = unidadeService.getCodById(idUo);
            String codPo = planoService.getCodById(idPo);
            
            List<Map<String, JsonNode>> resultList = investimentosBIService.getCardsTotais(
                    null, 
                    ano, 
                    codUo, 
                    codPo
                );
            Map<String, JsonNode> linhaResultado = resultList.get(0);

            
            
             return new CardsTotaisDto(
                totaisCusto.previsto(), 
                totaisCusto.contratado(), 
                linhaResultado.get("orcado").asDouble(), 
                linhaResultado.get("autorizado").asDouble(), 
                linhaResultado.get("empenhado").asDouble(), 
                linhaResultado.get("liquidado").asDouble(), 
                linhaResultado.get("disponivel_sem_reserva").asDouble(), 
                linhaResultado.get("pago").asDouble()
                );

        }
    

    
}

