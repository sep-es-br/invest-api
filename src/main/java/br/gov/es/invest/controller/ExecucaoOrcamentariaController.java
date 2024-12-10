package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.model.Ano;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.service.AnoService;
import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import lombok.RequiredArgsConstructor;


@CrossOrigin(origins = "${frontend.host}")
@RestController
@RequestMapping("/execucao")
@RequiredArgsConstructor
public class ExecucaoOrcamentariaController {
    
    
    private final ExecucaoOrcamentariaService service;
    private final InvestimentoService investimentoService;
    private final InvestimentosBIService investimentosBIService;
    private final AnoService anoService;
    private final FonteOrcamentariaService fonteOrcamentariaService;

    @GetMapping("/totalOrcado")
    public Double getTotalOrcado(@RequestParam String ano) {
        return service.getTotalOrcadoByAno(ano);
    }

    @GetMapping("/importarPentaho")
    public String importarPentaho(@RequestParam(required = false) Integer anoRef) {

        // se não receber o ano de Referencia, considera o ano corrente
        if(anoRef == null) {
            anoRef = LocalDate.now().getYear();
        }

        // Carrega os dados do pentaho
        List<Map<String, JsonNode>> dadosPorMes = investimentosBIService.getDadosPorMes(anoRef-1, anoRef);
        List<Map<String, JsonNode>> dadosPorAno = investimentosBIService.getDadosPorAno(anoRef, anoRef+1);

        // processa os dados

        // primeiro o mais facil, dados por ano
        for(Map<String, JsonNode> dado : dadosPorAno){
            // guarda valores nas variaveis
            String codPo = dado.get("cod_po").asText();
            String codUo = dado.get("cod_uo").asText();
            int ano = dado.get("ano").asInt();
            String codFonte = dado.get("cod_fonte").asText();
            String nomeFonte = dado.get("nome_fonte").asText();
            double orcado = dado.get("orcado").asDouble();
            double autorizado = dado.get("autorizado").asDouble();
            double dispSemReserva = dado.get("disponivel_sem_reserva").asDouble();

            // retorna o investimento no banco
            Investimento investimento = investimentoService.getByCodUoPo(codUo, codPo);

            // se não existir no banco passa pro proximo e nem perde tempo;
            if(investimento == null) continue;

            // busca um objeto de execução pré existente no ano e fonte indicada
            List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentariaDelimitadores().stream()
                .filter(exec -> {
                    return exec.getAnoExercicio().getAno().equals(Integer.toString(ano))
                        && exec.getFonteOrcamentariaVinculadora().getCodigo().equals(Integer.valueOf(codFonte));
                }).toList();
            
            ExecucaoOrcamentaria execucao = null;
            // se não existir, cria outra
            if(execs.isEmpty()){
                execucao = new ExecucaoOrcamentaria();
                Ano anoExercicio = anoService.findOrCreate(String.valueOf(ano));
                FonteOrcamentaria fonte = fonteOrcamentariaService.findOrCreate(Integer.valueOf(codFonte), nomeFonte);
                
                execucao.setAnoExercicio(anoExercicio);
                execucao.setFonteOrcamentariaVinculadora(fonte);

                execucao.setContaDelimitada(investimento);

                // salva no banco

            } else { // se existir atualiza a existente
                execucao = execs.get(0);
            }

            
            execucao.setOrcamento(orcado);
            execucao.setAutorizado(autorizado);
            execucao.setDispSemReserva(dispSemReserva);

            service.save(execucao);
        }

        // agora começa a brincadeira
        for(Map<String, JsonNode> dado : dadosPorMes) {
            int ano = dado.get("ano").asInt();
            int liquidado = dado.get("liquidado").asInt();
            int empenhado = dado.get("empenhado").asInt();
            String codFonte = dado.get("cod_fonte").asText();
            int mes = dado.get("mes").asInt();
            String codUo = dado.get("cod_uo").asText();
            String codPo = dado.get("cod_po").asText();
            int pago = dado.get("pago").asInt();
            String nomeFonte = dado.get("nome_fonte").asText();

            
            // retorna o investimento no banco
            Investimento investimento = investimentoService.getByCodUoPo(codUo, codPo);

            // se não existir no banco passa pro proximo e nem perde tempo;
            if(investimento == null) continue;

            // busca um objeto de execução pré existente no ano e fonte indicada
            List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentariaDelimitadores().stream()
                .filter(exec -> {
                    return exec.getAnoExercicio().getAno().equals(Integer.toString(ano))
                        && exec.getFonteOrcamentariaVinculadora().getCodigo().equals(Integer.valueOf(codFonte));
                }).toList();
            
            ExecucaoOrcamentaria execucao = null;
            // se não existir, cria outra
            if(execs.isEmpty()){
                execucao = new ExecucaoOrcamentaria();
                Ano anoExercicio = anoService.findOrCreate(String.valueOf(ano));
                FonteOrcamentaria fonte = fonteOrcamentariaService.findOrCreate(Integer.valueOf(codFonte), nomeFonte);

                execucao.setAnoExercicio(anoExercicio);
                execucao.setFonteOrcamentariaVinculadora(fonte);

                execucao.setContaDelimitada(investimento);

                // salva no banco

            } else { // se existir atualiza a existente
                execucao = execs.get(0);
            }

            execucao.getLiquidado()[mes-1] = liquidado;
            execucao.getEmpenhado()[mes-1] = empenhado;
            execucao.getPago()[mes-1] = pago;

            service.save(execucao);

        }

        return "Sucesso";

    }
}
