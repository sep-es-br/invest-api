package br.gov.es.invest.controller;

import br.gov.es.invest.dto.CardsTotaisDto;
import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.SetorDto;
import br.gov.es.invest.dto.ValoresCusto;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.service.ACService;
import br.gov.es.invest.service.AnoService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import br.gov.es.invest.service.InfosService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/infos")
@RequiredArgsConstructor
public class InfosController {

    private final InvestimentosBIService investimentosBIService;

    private final InfosService service;
    private final AnoService anoService;
    private final ACService aCService;
    private final UnidadeOrcamentariaService unidadeService;
    private final PlanoOrcamentarioService planoService;
    private final FonteOrcamentariaService fonteService;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    
    @PostMapping("/frontendError")
    public void printFrontendError(
            @RequestBody String error
    ){
        Logger.getGlobal().severe("frontend error: " + error);
    }

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
        return aCService.getPapeis(setorGuid).stream()
                .sorted((p1, p2) -> p1.agenteNome().compareToIgnoreCase(p2.agenteNome()))
                .collect(Collectors.toList());
    }

    @GetMapping("/cardsTotais")
    public ResponseEntity<?> getCardsTotais(
        @RequestParam(required=false) String nome, @RequestParam Boolean podeVerUnidades, @RequestHeader("Authorization") String authToken,
        @RequestParam(required=false) String idUo, @RequestParam(required=false) Long idFonte,
        @RequestParam(required=false) String idPo, @RequestParam Integer ano, @RequestParam(required = false) Integer gnd
        ) {
            try {
                List<Long> idsUo = null;
                if(idUo == null && !podeVerUnidades) {
                    
                    authToken = authToken.replace("Bearer ", "");
            
                    String sub = tokenService.validarToken(authToken);
                            
                    Agente usuario = usuarioService.getUserBySub(sub).orElse(null);
                    
                   
                    List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId(), false);

                    idsUo = unidades.stream().map(u -> u.getId()).toList();
                } else if(idUo != null) {
                    idsUo = new JsonMapper().readValue(idUo, new TypeReference<List<Long>>() {});
                }
                List<Long> idsPo = idPo == null ? null : new JsonMapper().readValue(idPo, new TypeReference<List<Long>>() {})
                                       ;

                ValoresCusto totaisCusto = service.getTotaisInvestimento(nome, idFonte, ano, idsUo, idsPo, gnd);    
              
                ArrayList<String> codsUo = new ArrayList<>();
                ArrayList<String> codsPo = new ArrayList<>();

                if(idsUo != null)
                    for(Long idUoS : idsUo) {
                        codsUo.add(unidadeService.getCodById(idUoS));
                    }
                
                if(idsPo != null)
                    for(Long idPoS : idsPo) {
                        codsPo.add(planoService.getCodById(idPoS));
                    }
                String codUo = idsUo == null ? null : String.join(",", codsUo) ;
                String codPo = idsPo == null ? null : String.join(",", codsPo);

                String codFonte = fonteService.getCodById(idFonte);
    
                codFonte = codFonte == null ? null : String.valueOf(Integer.parseInt(codFonte)); 
    
                
                List<Map<String, JsonNode>> resultList = investimentosBIService.getCardsTotais(
                        codFonte, 
                        ano, 
                        codUo, 
                        codPo,
                        gnd
                    );
                Map<String, JsonNode> linhaResultado = resultList.get(0);
    
                
                
                 return ResponseEntity.ok(new CardsTotaisDto(
                    totaisCusto.planejado(), 
                    totaisCusto.contratado(), 
                    linhaResultado.get("orcado").asDouble(), 
                    linhaResultado.get("autorizado").asDouble(), 
                    linhaResultado.get("empenhado").asDouble(), 
                    linhaResultado.get("liquidado").asDouble(), 
                    linhaResultado.get("disponivel_sem_reserva").asDouble(), 
                    linhaResultado.get("pago").asDouble()
                    ));
            } catch (JsonProcessingException | NumberFormatException ex) {
                Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                return MensagemErroRest.asResponseEntity(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erro ao calcular cards", 
                    Arrays.asList(ex.getLocalizedMessage())
                );
            }
            

        }
    

    
}

