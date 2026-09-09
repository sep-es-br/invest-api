package br.gov.es.invest.service;

import br.gov.es.invest.exception.SemApontamentosException;
import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Parecer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcaoService {
    

    private ApontamentoService apontamentoService;
    private ObjetoService objetoService;

    @Transactional
    public Objeto executarAcao(Objeto objeto, List<Apontamento> apontamentos, Parecer parecer, Acao acao, Agente usuario) throws SemApontamentosException{
        
        if(acao.getPositivo() != null && apontamentos != null && !acao.getPositivo() && acao.getProxEtapa() != null && apontamentos.isEmpty())
            throw new SemApontamentosException();

        ZonedDateTime agora = ZonedDateTime.now();
        
        Objeto objetoOriginal = objetoService.findById(objeto.getId());
        objetoOriginal.aplicar(objeto);
        
        
        if(acao.getProxEtapa() == null) { // ponta do fluxo
            if(acao.getPositivo()) { // ação positiva significa que terminou o fluxo
                
                objetoOriginal = objetoService.save(objetoOriginal);
                
                objetoService.alterarStatus(objetoOriginal.getId(), acao.getStatusFinal().getId(), agora);
                objetoService.updateUltimaEtapa(objetoOriginal.getId(), agora, usuario.getId());

                
                return objetoService.findById(objetoOriginal.getId());
            } else { // se não significa que o fluxo foi cancelado
                return objetoService.removerObjeto(objeto.getId());
            }

        } else { // meio do fluxo
            if(acao.getPositivo() != null && !acao.getPositivo()){         

                if(parecer != null) {

                    parecer.setEtapa(acao.getProxEtapa());
                    parecer.setGrupo(objeto.getEtapaAtual().getEtapa().getGrupoResponsavel());
                    parecer.setTimestamp(agora);
                    parecer.setUsuario(usuario);

                    ArrayList<Parecer> todosPareceres = new ArrayList<>(
                        objetoOriginal.getPareceres() == null ? Arrays.asList() : objetoOriginal.getPareceres()
                    ); 
                    todosPareceres.add(parecer);
                    objetoOriginal.setPareceres(todosPareceres);


                } else if (apontamentos != null) {
                    // A relação existente é a fonte de verdade; o objeto recebido na
                    // requisição contém apenas os dados cadastrais.
                    List<Apontamento> apontamentosAtuais = new ArrayList<>(
                            apontamentoService.findByObjeto(objetoOriginal.getId())
                    );
                    objetoOriginal.setApontamentos(apontamentosAtuais);

                    Set<Long> idsMantidos = apontamentos.stream()
                            .map(Apontamento::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    Set<Apontamento> apontamentosRemovidos = apontamentosAtuais.stream()
                            .filter(a -> a.getId() != null && !idsMantidos.contains(a.getId()))
                            .collect(Collectors.toSet());

                    apontamentosAtuais.removeAll(apontamentosRemovidos);

                    for(Apontamento removido : apontamentosRemovidos){
                        apontamentoService.remover(removido);
                    }

                    // 5. Persiste cada apontamento e garante explicitamente a relação
                    // com o objeto. Os apontamentos existentes permanecem na coleção;
                    // os novos são adicionados somente após receberem o ID do banco.
                    for (Apontamento apontamento : apontamentos) {
                        if (apontamento.getId() == null) {
                            apontamento.setEtapa(acao.getProxEtapa());
                            apontamento.setGrupo(objeto.getEtapaAtual().getEtapa().getGrupoResponsavel());
                            apontamento.setTimestamp(agora);
                            apontamento.setUsuario(usuario);
                            apontamento.setActive(true);

                            Apontamento salvo = apontamentoService.mergeObjetoApontamento(apontamento, objetoOriginal);
                            apontamentosAtuais.add(salvo);
                        } else {
                            for (Apontamento atual : apontamentosAtuais) {
                                if (apontamento.getId().equals(atual.getId())) {
                                    apontamentoService.mergeObjetoApontamento(atual, objetoOriginal);
                                    break;
                                }
                            }
                        }
                    }
                }

                
            }
            
            objetoService.save(objetoOriginal);
            
            objetoService.updateUltimaEtapa(objetoOriginal.getId(), agora, usuario.getId());
            
            objetoService.addEtapa(objetoOriginal.getId(), acao.getProxEtapa().getId(), !acao.getPositivo(), acao.getAtividadeFinal(), agora);
            
            objetoService.alterarStatus(objetoOriginal.getId(), acao.getStatusFinal().getId(), agora);
            
            return objetoService.findById(objeto.getId());
        }
        
    }

    @Autowired
    public void setApontamentoService(ApontamentoService apontamentoService) {
        this.apontamentoService = apontamentoService;
    }

    @Autowired
    public void setObjetoService(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }

    

}
