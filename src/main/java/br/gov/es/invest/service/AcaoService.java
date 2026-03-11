package br.gov.es.invest.service;

import br.gov.es.invest.exception.SemApontamentosException;
import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Parecer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                
                EmStatus emStatusTarget = new EmStatus();
                emStatusTarget.setStatus(acao.getStatusFinal());
                emStatusTarget.setTimestamp(agora);
                
                objetoOriginal.setEmStatus(null);
                
                objetoService.save(objetoOriginal);

                objetoOriginal.setEmStatus(emStatusTarget); // aplica status final

                return objetoService.save(objetoOriginal);
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


                } else if(apontamentos != null) {


                    List<Apontamento> apontamentosAtuais = objetoOriginal.getApontamentos();
                    List<Apontamento> apontamentosRemovidos = apontamentosAtuais.stream()
                    .filter( apontamento -> {
                            return !apontamentos.stream().map(a -> a.getId()).toList().contains(apontamento.getId());
                        } ).toList();

                    for(Apontamento removido : apontamentosRemovidos){
                        apontamentoService.remover(removido);
                    }

                    for(Apontamento apontamento : apontamentos.stream().filter(a -> a.getId() == null).toList()) {
    
                        apontamento.setEtapa(acao.getProxEtapa());
                        apontamento.setGrupo(objeto.getEtapaAtual().getEtapa().getGrupoResponsavel());
                        apontamento.setTimestamp(agora);
                        apontamento.setUsuario(usuario);
                        apontamento.setActive(true);
                        
                    }
    
                    objetoOriginal.setApontamentos(apontamentos);
                }

                
            }
            
            objetoOriginal.getEtapaAtual().setAvaliadoEm(agora);
            objetoOriginal.getEtapaAtual().setAvaliadoPorId(usuario.getId());
            
            EmEtapa emEtapaTarget = new EmEtapa();
            emEtapaTarget.setDevolvido(!acao.getPositivo());
            emEtapaTarget.setEtapa(acao.getProxEtapa());
            emEtapaTarget.setAtividade(acao.getAtividadeFinal());
            emEtapaTarget.setTimestamp(agora);
            
            objetoOriginal.getEmEtapa().add(emEtapaTarget);
            
             
            EmStatus emStatusTarget = new EmStatus();
            emStatusTarget.setStatus(acao.getStatusFinal());
            emStatusTarget.setTimestamp(agora);

            objetoOriginal.setEmStatus(emStatusTarget);
            objetoService.save(objetoOriginal);
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
