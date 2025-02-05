package br.gov.es.invest.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.EtapaDTO;
import br.gov.es.invest.dto.FluxoDTO;
import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.AcaoEnum;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.service.FluxoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/fluxo")
@RequiredArgsConstructor
public class FluxoController {
    
    private final FluxoService fluxoService;

    @GetMapping("")
    public ResponseEntity<?> find(@RequestParam(required = false) String id) {

        if(id == null) {
            List<FluxoDTO> fluxoDTOs = fluxoService.findAll().stream()
                                    .map(fluxo -> new FluxoDTO(fluxo))
                                    .toList();

            return ResponseEntity.ok(fluxoDTOs);
        }

        Fluxo fluxo = fluxoService.findById(id);

        if(fluxo == null) {
            return MensagemErroRest.asResponseEntity(
                HttpStatus.NOT_FOUND, 
                "fluxoInexistente", 
                null
            );
        }

        return ResponseEntity.ok(FluxoDTO.parse(fluxo));
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

        return ResponseEntity.ok(FluxoDTO.parse(fluxo));


    }

    // @PostMapping("/gerar")
    // public String postMethodName() {
    //     //TODO: process POST request

    //     Status statusSolicitado = new Status();
    //     statusSolicitado.setNome("Solicitado");

    //     Status devolvidoStatus = new Status();
    //     devolvidoStatus.setNome("Devolvido");

    //     Status emAprovacaoStatus = new Status();
    //     emAprovacaoStatus.setNome("Em Aprovação");

    //     Status finalizandoStatus = new Status();
    //     finalizandoStatus.setNome("Finalizando");

    //     Status emAnaliseStatus = new Status();
    //     emAnaliseStatus.setNome("Em Análise");

    //     Status cadastradoStatus = new Status();
    //     cadastradoStatus.setNome("Cadastrado");
        
    //     Etapa solicitacaoEtapa = new Etapa();
    //     solicitacaoEtapa.setNome("Solicitação Cadastro");
    //     solicitacaoEtapa.setOrdem(0);

    //     Etapa analTecnica = new Etapa();
    //     analTecnica.setNome("Análise Técnica");
    //     analTecnica.setOrdem(1);

    //     Etapa aprSubeo = new Etapa();
    //     aprSubeo.setNome("Aprovação Subeo");
    //     aprSubeo.setOrdem(2);

    //     Etapa cadPO = new Etapa();
    //     cadPO.setNome("Cadastro P.O");
    //     cadPO.setOrdem(3);

    //     Acao solEtapaReenviarAcao = new Acao();
    //     solEtapaReenviarAcao.setNome("Reenviar");
    //     solEtapaReenviarAcao.setPositivo(true);
    //     solEtapaReenviarAcao.setAcaoId(AcaoEnum.REENVIAR);
    //     solEtapaReenviarAcao.setAtividadeFinal("Efetuar análise técnica");
    //     solEtapaReenviarAcao.setStatusFinal(statusSolicitado);
    //     solEtapaReenviarAcao.setProxEtapa(analTecnica);

    //     Acao excluirAcao = new Acao();
    //     excluirAcao.setNome("Excluir");
    //     excluirAcao.setPositivo(false);
    //     excluirAcao.setAcaoId(AcaoEnum.EXCLUIR_SOLICITACAO);

    //     solicitacaoEtapa.setAcoes(Arrays.asList(solEtapaReenviarAcao, excluirAcao));

    //     Acao devolver1 = new Acao();
    //     devolver1.setAcaoId(AcaoEnum.DEVOLVER_SOLICITACAO);
    //     devolver1.setPositivo(false);
    //     devolver1.setNome("Devolver");
    //     devolver1.setAtividadeFinal("Corrigir Solicitação");
    //     devolver1.setStatusFinal(devolvidoStatus);
    //     devolver1.setProxEtapa(solicitacaoEtapa);

        
    //     Acao aprovar1 = new Acao();
    //     aprovar1.setAcaoId(AcaoEnum.APROVAR_SOLICITACAO);
    //     aprovar1.setPositivo(true);
    //     aprovar1.setNome("Aprovar");
    //     aprovar1.setAtividadeFinal("Efetuar Aprovação");
    //     aprovar1.setStatusFinal(emAprovacaoStatus);
    //     aprovar1.setProxEtapa(aprSubeo);

    //     analTecnica.setAcoes(Arrays.asList(devolver1, aprovar1));

    //     Acao devolver2 = new Acao();
    //     devolver2.setAcaoId(AcaoEnum.DEVOLVER);
    //     devolver2.setPositivo(false);
    //     devolver2.setNome("Devolver");
    //     devolver2.setAtividadeFinal("Retificar Solicitação");
    //     devolver2.setStatusFinal(devolvidoStatus);
    //     devolver2.setProxEtapa(analTecnica);

        
    //     Acao aprovar2 = new Acao();
    //     aprovar2.setAcaoId(AcaoEnum.APROVAR);
    //     aprovar2.setPositivo(true);
    //     aprovar2.setNome("Aprovar");
    //     aprovar2.setAtividadeFinal("Cadastro P.O.");
    //     aprovar2.setStatusFinal(finalizandoStatus);
    //     aprovar2.setProxEtapa(cadPO);

    //     aprSubeo.setAcoes(Arrays.asList(devolver2, aprovar2));

    //     Acao devolver3 = new Acao();
    //     devolver3.setAcaoId(AcaoEnum.APONTAMENTO);
    //     devolver3.setPositivo(false);
    //     devolver3.setNome("Fazer Apontamento");
    //     devolver3.setAtividadeFinal("Analisar Apontamento");
    //     devolver3.setStatusFinal(emAprovacaoStatus);
    //     devolver3.setProxEtapa(aprSubeo);

        
    //     Acao aprovar3 = new Acao();
    //     aprovar3.setAcaoId(AcaoEnum.INCLUIR_PO);
    //     aprovar3.setPositivo(true);
    //     aprovar3.setNome("Cadastrar P.O.");
    //     aprovar3.setStatusFinal(cadastradoStatus);

    //     cadPO.setAcoes(Arrays.asList(devolver3, aprovar3));

    //     Fluxo fluxo = new Fluxo();
    //     fluxo.setNome("Fluxo de Avaliação - PIP");
    //     fluxo.setFluxoId("avaliacaoPip");
    //     fluxo.setEtapaInicial(analTecnica);
    //     fluxo.setEtapas(Arrays.asList(solicitacaoEtapa, analTecnica, aprSubeo, cadPO));

    //     fluxoService.salvarFluxo(fluxo);

    //     return "Teste";
    // }
    

}
