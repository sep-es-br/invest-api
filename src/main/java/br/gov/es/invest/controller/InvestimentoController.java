package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.InvestimentoTiraDTO;
import br.gov.es.invest.dto.projection.TiraInvestimentoProjection;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import br.gov.es.invest.utils.DataListResult;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;

import br.gov.es.invest.dto.FiltroInvestimentoDto;
import br.gov.es.invest.dto.PlanoOrcamentarioDTO;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;



@RestController
@RequestMapping("/investimento")
@RequiredArgsConstructor
public class InvestimentoController {


    private final InvestimentoService service;

    private final ObjetoService objetoService;
    private final UsuarioService usuarioService;
    private final UnidadeOrcamentariaService unidadeOrcamentariaService;
    private final TokenService tokenService;
    
    @PostMapping("filtrarValores")    
    public ResponseEntity<?> getAllTiraByFilter(
            @RequestBody FiltroInvestimentoDto filtro, @RequestHeader("Authorization") String authToken
        ) {                
            
            List<String> idsUo = null;
            if(filtro.unidades() == null && !filtro.podeVerUnidades()) {
                
                authToken = authToken.replace("Bearer ", "");
        
                String sub = tokenService.validarToken(authToken);
                        
                Usuario usuario = usuarioService.getUserBySub(sub).orElse(null);
                
                List<UnidadeOrcamentaria> unidades = unidadeOrcamentariaService.findByAgente(usuario.getId());

                idsUo = unidades.stream().map(UnidadeOrcamentaria::getId).toList();
            } else if(filtro.unidades() != null) {
                idsUo = filtro.unidades().stream().map(UnidadeOrcamentariaDTO::id).toList();
            }

        
            List<String> idsPo = filtro.planos() == null ? null : filtro.planos().stream().map(PlanoOrcamentarioDTO::id).toList();
        
        
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
    
    
}
