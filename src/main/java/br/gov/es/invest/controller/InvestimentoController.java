package br.gov.es.invest.controller;

import br.gov.es.invest.dto.FiltroInvestimentoDto;
import br.gov.es.invest.dto.InvestimentoTiraDTO;
import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.investimento.InvestimentoCadastroDto;
import br.gov.es.invest.dto.investimento.InvestimentoDetailDto;
import br.gov.es.invest.dto.investimento.InvestimentoListaDto;
import br.gov.es.invest.dto.projection.TiraInvestimentoProjection;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.factory.InvestimentoFactory;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DataListResult;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/investimento")
@RequiredArgsConstructor
public class InvestimentoController {


    private final InvestimentoService service;
    private final InvestimentoFactory investimentoFactory;

    private final ObjetoService objetoService;
    private final UsuarioService usuarioService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    private final TokenService tokenService;
    
    @PostMapping("filtrarValores")    
    public ResponseEntity<?> getAllTiraByFilter(
            @RequestBody FiltroInvestimentoDto filtro, @RequestHeader("Authorization") String authToken
        ) {                
            
            List<Long> idsUo = null;
            if(filtro.unidades() == null && !filtro.podeVerUnidades()) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

                idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
            } else if(filtro.unidades() != null) {
                idsUo = filtro.unidades().stream().map(UnidadeOrcamentariaDTO::id).toList();
            }

        
            List<Long> idsPo = filtro.planos() == null ? null : filtro.planos().stream().map(PlanoOrcamentarioDTO::id).toList();
        
        
            DataListResult<TiraInvestimentoProjection> dataList = service.findAllTiraBy(
                filtro.nome(), idsUo, idsPo, filtro.ano(), filtro.fonte() == null ? null : filtro.fonte().getId(), 
                filtro.gnd(), filtro.ordem(), PageRequest.of(filtro.numPag()-1, filtro.qtPorPag())
            );
            
            
            DataListResult<InvestimentoTiraDTO> dataListDto = new DataListResult<>(
                dataList.data().stream().map(investimento -> {
                    return InvestimentoTiraDTO.parse(investimento, 
                            objetoService.findObjetoCadastradoByContaBy(investimento.id(), filtro.ano(), filtro.fonte() == null ? null : filtro.fonte().getId(), filtro.gnd(), null)
                        );
                }).toList(), 
                dataList.ammount()
            );

            return ResponseEntity.ok(dataListDto);
    }
    
    
    @GetMapping
    public DataListResult<InvestimentoListaDto> getInvestimentos(
            @RequestParam Boolean podeVerUnidades,
            @RequestParam(required = false) String term,
            @RequestParam Integer numPag,
            @RequestParam Integer tamPag,
            @RequestHeader("Authorization") String authToken
    ) {
        List<Long> idsUo = null;
        if(!podeVerUnidades) {

            authToken = authToken.replace("Bearer ", "");

            String sub = tokenService.validarToken(authToken);

            Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);

            List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

            idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
        }
        
        return service.findAllLista(term, idsUo, PageRequest.of(numPag, tamPag));

    }
    
    @GetMapping("{id}")
    public ResponseEntity<InvestimentoDetailDto> getInvestimento(
            @PathVariable Long id
    ){
        
        return ResponseEntity.of(service.getById(id).map(this.investimentoFactory::toInvestimentoDetalDto));
        
    }
    
    @PostMapping("")
    public ResponseEntity<InvestimentoDetailDto> setInvestimento(
            @RequestBody InvestimentoCadastroDto novoInvestimento,
            @RequestHeader("Authorization") String authToken
    ) {
        
        authToken = authToken.replace("Bearer ", "");

        String sub = tokenService.validarToken(authToken);

        Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
        
        Investimento investimento = investimentoFactory.toInvestimento(novoInvestimento, usuario);
        
        return ResponseEntity.ok(investimentoFactory.toInvestimentoDetalDto(service.save(investimento)));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerInvestimento(
            @PathVariable Long id
    ){
        Investimento investimento = service.getById(id).orElseThrow();
        
        if(investimento.getObjetos() != null && !investimento.getObjetos().isEmpty()) {
            return MensagemErroRest.asResponseEntity(
                    HttpStatus.UNPROCESSABLE_ENTITY, 
                    "Não pode remover investimentos com objetos", 
                    Arrays.asList("Não pode remover investimentos com objetos")
            );
        }
        
        this.service.removerInvestimento(id);
        
        return ResponseEntity.ok(null);
        
    }
    
    
}
