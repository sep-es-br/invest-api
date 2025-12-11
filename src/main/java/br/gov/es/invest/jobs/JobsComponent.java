/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.jobs;

import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.VinculadaPor;
import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class JobsComponent {

    private static final Logger LOGGER = Logger.getGlobal();

    private final ExecucaoOrcamentariaService service;
    private final InvestimentoService investimentoService;
    private final InvestimentosBIService investimentosBIService;
    private final FonteOrcamentariaService fonteOrcamentariaService;
    private final PlanoOrcamentarioService planoOrcamentarioService;
    
    @Value("${server.job.importarPentaho.ano}")
    private Integer importarAno;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void init() {
//        this.doImportarPentaho(Optional.ofNullable(importarAno).orElse(LocalDate.now().getYear()));;
    }

    @Scheduled(cron = "${server.job.importarPentaho.cron}")
    public void triggerImportarPentaho() {
        this.doImportarPentaho(Optional.ofNullable(importarAno).orElse(LocalDate.now().getYear()));
    }

    private void doImportarPentaho(Integer anoRef) {

        ByteArrayOutputStream logOut = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(logOut, StandardCharsets.UTF_8);
        PrintWriter pw = new PrintWriter(writer, true);
        StreamHandler memHandler = new StreamHandler(logOut, new SimpleFormatter());

        LOGGER.addHandler(memHandler);
        LOGGER.setLevel(Level.INFO);

        try {
            // atualiza o nome dos planosOrcamentario
            planoOrcamentarioService.atualizarNomesComBi();

            // Carrega os dados do pentaho
            List<Map<String, JsonNode>> dadosPorMes = investimentosBIService.getDadosPorMes(anoRef - 1, anoRef);
            List<Map<String, JsonNode>> dadosPorAno = investimentosBIService.getDadosPorAno(anoRef, anoRef + 1);

            // processa os dados
            // seta tudo como sujo
            service.setaTudoNovo(anoRef, false);
            service.setaTudoNovo(anoRef + 1, false);
            // primeiro o mais facil, dados por ano
            for (Map<String, JsonNode> dado : dadosPorAno) {
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

                LOGGER.log(Level.INFO, "consumindo: {0} - {1} em {2}", new Object[] { codUo, codPo, ano });

                FonteOrcamentaria fonteOrcamentaria = fonteOrcamentariaService
                        .findByCod(String.format("%09d", codTipoFonte));

                // retorna o investimento no banco
                Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(codUo, codPo);

                // se não existir no banco passa pro proximo e nem perde tempo;
                if (optInvestimento.isEmpty())
                    continue;

                // busca um objeto de execução pré existente no ano
                Investimento investimento = optInvestimento.get();
                List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentaria().stream()
                        .filter(exec -> {
                            return exec.getAnoExercicio().equals(ano);
                        }).toList();

                ExecucaoOrcamentaria execucao;
                // se não existir, cria outra
                if (execs.isEmpty()) {

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
                if (valoresList.isEmpty()) {
                    valores = new VinculadaPor();

                    valores.setFonteOrcamentaria(fonteOrcamentaria);

                    execucao.getVinculadaPor().add(valores);
                } else {
                    valores = valoresList.get(0);
                }

                // se não for novo, limpa os valores antigos
                if (!valores.isNovo()) {
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
            service.setaTudoNovo(anoRef - 1, false);
            service.setaTudoNovo(anoRef, false);

            // agora começa a brincadeira
            for (Map<String, JsonNode> dado : dadosPorMes) {
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

                LOGGER.log(Level.INFO, "consumindo: unidade: {0} - {1} em {2}/{3}",
                        new Object[] { codUo, codPo, String.format("%02d", mes), ano });

                FonteOrcamentaria fonteOrcamentaria = fonteOrcamentariaService
                        .findByCod(String.format("%09d", codTipoFonte));

                // retorna o investimento no banco
                Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(codUo, codPo);

                // se não existir no banco passa pro proximo e nem perde tempo;
                if (optInvestimento.isEmpty())
                    continue;

                Investimento investimento = optInvestimento.get();
                List<ExecucaoOrcamentaria> execs = investimento.getExecucoesOrcamentaria().stream()
                        .filter(exec -> {
                            return exec.getAnoExercicio().equals(ano);
                        }).toList();

                ExecucaoOrcamentaria execucao;
                // se não existir, cria outra
                if (execs.isEmpty()) {

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
                if (valoresList.isEmpty()) {
                    valores = new VinculadaPor();

                    valores.setFonteOrcamentaria(fonteOrcamentaria);

                    execucao.getVinculadaPor().add(valores);
                } else {
                    valores = valoresList.get(0);
                }

                // garantir que os campos tem os 12 espaços

                if (!valores.isNovo()) {
                    valores.setLiquidado(new double[12]);
                    valores.setEmpenhado(new double[12]);
                    valores.setPago(new double[12]);
                    valores.setNovo(true);
                }

                valores.getLiquidado()[mes - 1] += (double) liquidado;
                valores.getEmpenhado()[mes - 1] += (double) empenhado;
                valores.getPago()[mes - 1] += (double) pago;
                valores.setGnd(Integer.parseInt(codGnd));

                service.save(execucao);

            }

            LOGGER.info("Migração do Sigefes concluida");

            memHandler.flush();

            LOGGER.removeHandler(memHandler);
            memHandler.close();

        } catch (Exception e) {

            LOGGER.removeHandler(memHandler);
            memHandler.close();

            Logger.getGlobal().log(Level.SEVERE, e.getLocalizedMessage(), e);

        }
    }

}
