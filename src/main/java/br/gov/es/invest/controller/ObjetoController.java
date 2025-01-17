package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ObjetoTiraDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.dto.ContaDto;
import br.gov.es.invest.dto.ObjetoDto;
import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.service.ContaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.ObjetoService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import br.gov.es.invest.service.TokenService;
import br.gov.es.invest.service.UnidadeOrcamentariaService;
import br.gov.es.invest.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/objeto")
@RequiredArgsConstructor
public class ObjetoController {

    @Value("${frontend.host}")
    private String frontHost;

    private final Logger logger = Logger.getLogger("ObjetoController");

    private final ObjetoService service;
    private final InvestimentoService investimentoService;
    private final UnidadeOrcamentariaService unidadeService;
    private final PlanoOrcamentarioService planoService;
    private final ContaService contaService;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    @GetMapping("/allTira")
    public ResponseEntity<List<ObjetoTiraDTO>> getAllByFiltro(
        @RequestParam Integer exercicio,@RequestParam(required = false) String nome,
        @RequestParam(required = false) String idUnidade, @RequestParam(required = false) String idPo,@RequestParam(required = false) String status,
        @RequestParam int pgAtual, @RequestParam int tamPag 
    ) {

        try{

            List<Objeto> objetos = service.getAllByFilter(exercicio, nome, idUnidade, idPo, status, PageRequest.of(pgAtual-1, tamPag));

            List<ObjetoTiraDTO> objetosDTO = objetos.stream().map(obj -> {                
                return new ObjetoTiraDTO(obj);
            }).toList();

            return ResponseEntity.ok(objetosDTO);
        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/byId")
    public ResponseEntity<ObjetoDto> getById(@RequestParam String id) {

        try{

            Optional<Objeto> optObjeto = service.getById(id);

            if(optObjeto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(new ObjetoDto(optObjeto.get()));

        } catch(Exception e){
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("")
    public ResponseEntity<ObjetoDto> cadastrarObjeto(@RequestBody ObjetoDto objetoDto, @RequestHeader("Authorization") String auth ) {
        
        Objeto objeto = new Objeto(objetoDto);
        UnidadeOrcamentaria unidade = unidadeService.findOrCreateByCod(new UnidadeOrcamentaria(objetoDto.conta().unidadeOrcamentariaImplementadora()));
        
        if(objeto.getResponsavel() == null) {
            auth = auth.replace("Bearer ", "");

            String sub = tokenService.validarToken(auth);

            objeto.setResponsavel( usuarioService.getUserBySub(sub).orElse(null) );
        }


        // define o Investimento que vai ser associado

        // se não tiver PO usa o investimento generico

        Conta conta = null;
        if(objetoDto.conta().planoOrcamentario() == null) {
            conta = contaService.getGenericoByCodUnidade(unidade);
        } else { // se não, busca o investimento

            Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(objetoDto.conta().unidadeOrcamentariaImplementadora().codigo(), objetoDto.conta().planoOrcamentario().codigo());
            Investimento investimento;

            if(optInvestimento.isEmpty()){ // se não existir, cria um novo

                    PlanoOrcamentario plano = planoService.findOrCreateByCod(new PlanoOrcamentario(objetoDto.conta().planoOrcamentario()));

                    investimento = new Investimento();
                    investimento.setNome(objetoDto.nome());
                    investimento.setUnidadeOrcamentariaImplementadora(unidade);
                    investimento.setPlanoOrcamentario(plano);
            } else { // se existir usa o existente
                investimento = optInvestimento.get();
            }
            
            conta = investimento;

        }
        
        objeto.setConta(conta);
        
        service.save(objeto);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteObj(String objetoId){
        
        Optional<Objeto> optObjetoRemovido = service.getById(objetoId);

        if(optObjetoRemovido.isEmpty())
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NO_CONTENT, 
                "Objeto não encontrado", 
                null
                );

        service.findObjetoByConta(optObjetoRemovido.get().getConta());

        if(service.findObjetoByConta(optObjetoRemovido.get().getConta()).size() == 1) {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                "Não foi possivel remover o objeto por ser o unico da despesa, uma despesa deve ter ao menos 1 objeto",
                null
                );
            
        }
        
        service.removerObjeto(objetoId);

        return ResponseEntity.ok(new ObjetoDto( optObjetoRemovido.get()));

    }


    @GetMapping("/statusCadastrado")
    public List<String> findStatusCadastrados() {
        return service.findStatusCadastrados();
    }
    
    

    @GetMapping("/countInvestimentoFiltro")
    public ResponseEntity<Integer> getAmmoutByInvestimentoFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String codUnidade, @RequestParam(required = false) String codPO,
        @RequestParam Integer exercicio
    ) {
        return ResponseEntity.ok(service.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAmmoutByFilter(
        @RequestParam(required = false) String nome, @RequestParam(required = false) String idUnidade,
        @RequestParam Integer exercicio, @RequestParam(required = false) String idPo, @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(service.countByFilter(nome, idUnidade, idPo, status, exercicio));
    }
    
}
