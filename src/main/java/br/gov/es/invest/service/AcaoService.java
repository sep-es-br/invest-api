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

@Service
public class AcaoService {
    

    private ApontamentoService apontamentoService;
    private ObjetoService objetoService;

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
                        objeto.getPareceres() == null ? Arrays.asList() : objeto.getPareceres()
                    ); 
                    todosPareceres.add(parecer);
                    objetoOriginal.setPareceres(todosPareceres);


                } else if (apontamentos != null) {
                    // 1. Obter a lista gerenciada atual
                    List<Apontamento> apontamentosAtuais = objetoOriginal.getApontamentos();
                    if (apontamentosAtuais == null) {
                        apontamentosAtuais = new ArrayList<>();
                        objetoOriginal.setApontamentos(apontamentosAtuais);
                    }

                    // 2. Mapeia os IDs dos apontamentos que vieram na requisição
                    Set<Long> idsMantidos = apontamentos.stream()
                            .map(Apontamento::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    // 3. Remove da coleção atual do grafo os que foram deletados
                    // No SDN, remover o elemento da lista faz o Cypher deletar a aresta (RELATIONSHIP) ao salvar
                    
                    Set<Apontamento> apontamentosRemovidos = apontamentosAtuais.stream()
                            .filter(a -> a.getId() != null && !idsMantidos.contains(a.getId()))
                            .collect(Collectors.toSet());
                    
                    apontamentosAtuais.removeAll(apontamentosRemovidos);

                    // 4. Deleta explicitamente no banco se tiver serviço próprio (opcional dependendo da sua regra)
                    for (Apontamento removido : apontamentosRemovidos) {
                        apontamentoService.remover(removido);
                    }

                    // 5. Adiciona os novos elementos na lista EXISTENTE do objeto
                    for (Apontamento novoApontamento : apontamentos) {
                        if (novoApontamento.getId() == null) {
                            novoApontamento.setEtapa(acao.getProxEtapa());
                            novoApontamento.setGrupo(objeto.getEtapaAtual().getEtapa().getGrupoResponsavel());
                            novoApontamento.setTimestamp(agora);
                            novoApontamento.setUsuario(usuario);
                            novoApontamento.setActive(true);

                            // Adiciona na coleção original do nó pai
                            apontamentosAtuais.add(novoApontamento);
                        }
                    }

                    // 6. NÂO faça: objetoOriginal.setApontamentos(apontamentos);
                    // Salve o objeto pai no repositório Neo4j para persistir o grafo e as arestas
                    // objetoRepository.save(objetoOriginal);
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
