package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.VinculadaPor;
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


        // return "rotina desativada temporariamente";
        // se não receber o ano de Referencia, considera o ano corrente
        if(anoRef == null) {
            anoRef = LocalDate.now().getYear();
        }


        // Carrega os dados do pentaho
        List<Map<String, JsonNode>> dadosPorMes = investimentosBIService.getDadosPorMes(anoRef-1, anoRef);
        List<Map<String, JsonNode>> dadosPorAno = investimentosBIService.getDadosPorAno(anoRef, anoRef+1);

        // processa os dados

        // seta tudo como sujo
        service.setaTudoNovo(anoRef, false);
        service.setaTudoNovo(anoRef+1, false);
       

        // primeiro o mais facil, dados por ano
        for(Map<String, JsonNode> dado : dadosPorAno){
            // guarda valores nas variaveis
            String codPo = dado.get("cod_po").asText();
            String codUo = dado.get("cod_uo").asText();
            int ano = dado.get("ano").asInt();
            String codFonte = dado.get("cod_fonte").asText();
            String nomeFonte = dado.get("nome_fonte").asText();
            Integer codTipoFonte = dado.get("tipo_fonte").asInt();
            double orcado = dado.get("orcado").asDouble();
            double autorizado = dado.get("autorizado").asDouble();
            double dispSemReserva = dado.get("disponivel_sem_reserva").asDouble();
            String codGnd = dado.get("COD_GRUPO_DESPESA").asText();

            Logger.getGlobal().info("consumindo: " + codUo + " - " + codPo + " em " + ano);

            FonteOrcamentaria fonteOrcamentaria = fonteOrcamentariaService.findByCod(String.format("%09d", codTipoFonte));

            // retorna o investimento no banco
            Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(codUo, codPo);

            // se não existir no banco passa pro proximo e nem perde tempo;
            if(optInvestimento.isEmpty()) continue;

            // busca um objeto de execução pré existente no ano
            Investimento investimento = optInvestimento.get();
            List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentaria().stream()
                .filter(exec -> {
                    return exec.getAnoExercicio().equals(ano);
                }).toList();
            
            ExecucaoOrcamentaria execucao = null;
            // se não existir, cria outra
            if(execs.isEmpty()){
                
                execucao = new ExecucaoOrcamentaria();
                execucao.setAnoExercicio(ano);

                execucao = service.save(execucao);

                investimentoService.addExecucao(investimento.getId(), execucao.getId());

            } else { // se existir atualiza a existente
                execucao = execs.get(0);
            }

            

            List<VinculadaPor> valoresList = execucao.getVinculadaPor().stream().filter(vinculada -> {
                return vinculada.getFonteOrcamentaria().getCodigo().equals(fonteOrcamentaria.getCodigo());
            }).toList();

            VinculadaPor valores;
            if(valoresList.isEmpty()) {
                valores = new VinculadaPor();

                valores.setFonteOrcamentaria(fonteOrcamentaria);

                execucao.getVinculadaPor().add(valores);
            } else {
                valores = valoresList.get(0);
            }

            // se não for novo, limpa os valores antigos
            if(!valores.isNovo()){
                valores.setOrcado(0);
                valores.setAutorizado(0);
                valores.setDispSemReserva(0);
                valores.setNovo(true);
            }
            
            valores.setOrcado(valores.getOrcado() + orcado);
            valores.setAutorizado(valores.getAutorizado() + autorizado);
            valores.setDispSemReserva(valores.getDispSemReserva() + dispSemReserva);
            valores.setGnd(Integer.parseInt(codGnd));

            service.save(execucao);
        }

        
        // seta tudo como sujo
        service.setaTudoNovo(anoRef-1, false);
        service.setaTudoNovo(anoRef, false);

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
            Integer codTipoFonte = dado.get("tipo_fonte").asInt();
            String codGnd = dado.get("COD_GRUPO_DESPESA").asText();

            Logger.getGlobal().info("consumindo: unidade: " + codUo + " - " + codPo + " em " + String.format("%02d", mes) + "/" + ano);
            
            FonteOrcamentaria fonteOrcamentaria = fonteOrcamentariaService.findByCod(String.format("%09d", codTipoFonte));

            
            // retorna o investimento no banco
            Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(codUo, codPo);

            // se não existir no banco passa pro proximo e nem perde tempo;
            if(optInvestimento.isEmpty()) continue;

            Investimento investimento = optInvestimento.get();
            List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentaria().stream()
                .filter(exec -> {
                    return exec.getAnoExercicio().equals(ano);
                }).toList();
            
            ExecucaoOrcamentaria execucao = null;
            // se não existir, cria outra
            if(execs.isEmpty()){
                
                execucao = new ExecucaoOrcamentaria();
                execucao.setAnoExercicio(ano);

                execucao = service.save(execucao);

                investimentoService.addExecucao(investimento.getId(), execucao.getId());

            } else { // se existir atualiza a existente
                execucao = execs.get(0);
            }

            
            List<VinculadaPor> valoresList = execucao.getVinculadaPor().stream().filter(vinculada -> {
                return vinculada.getFonteOrcamentaria().getCodigo().equals(fonteOrcamentaria.getCodigo());
            }).toList();

            VinculadaPor valores;
            if(valoresList.isEmpty()) {
                valores = new VinculadaPor();

                valores.setFonteOrcamentaria(fonteOrcamentaria);

                execucao.getVinculadaPor().add(valores);
            } else {
                valores = valoresList.get(0);
            }

            // garantir que os campos tem os 12 espaços

            if(!valores.isNovo()){
                valores.setLiquidado(new double[12]);
                valores.setEmpenhado(new double[12]);
                valores.setPago(new double[12]);
                valores.setNovo(true);
            }
            
            valores.getLiquidado()[mes-1] += (double) liquidado;
            valores.getEmpenhado()[mes-1] += (double) empenhado;            
            valores.getPago()[mes-1] += (double) pago;
            valores.setGnd(Integer.parseInt(codGnd));

            service.save(execucao);

        }

        Logger.getGlobal().info("Migração do Sigefes concluida");
        return "Sucesso";

    }
}
