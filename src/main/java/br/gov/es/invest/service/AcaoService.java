package br.gov.es.invest.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.invest.exception.SemApontamentosException;
import br.gov.es.invest.model.Acao;
import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Usuario;

@Service
public class AcaoService {
    

    private ApontamentoService apontamentoService;
    private ObjetoService objetoService;

    @Transactional
    public Objeto executarAcao(Objeto objeto, List<Apontamento> apontamentos, Acao acao, Usuario usuario) throws SemApontamentosException{
        
        if(acao.getPositivo() != null && !acao.getPositivo() && apontamentos.isEmpty())
            throw new SemApontamentosException();

        ZonedDateTime agora = ZonedDateTime.now();
        
        if(acao.getProxEtapa() == null) { // ponta do fluxo
            if(acao.getPositivo()) { // ação positiva significa que terminou o fluxo
                EmStatus emStatusTarget = new EmStatus();
                emStatusTarget.setStatus(acao.getStatusFinal());
                emStatusTarget.setTimestamp(agora);

                objeto.setEmStatus(emStatusTarget); // aplica status final
                objeto.setEmEtapa(null); // remove objeto do fluxo
                return objetoService.save(objeto);
            } else { // se não significa que o fluxo foi cancelado
                return objetoService.removerObjeto(objeto.getId());
            }

        } else { // meio do fluxo
            if(acao.getPositivo() != null && !acao.getPositivo()){         

                for(Apontamento apontamento : apontamentos) {
    
                    apontamento.setEtapa(acao.getProxEtapa());
                    apontamento.setGrupo(objeto.getEmEtapa().getEtapa().getGrupoResponsavel());
                    apontamento.setTimestamp(agora);
                    apontamento.setUsuario(usuario);
                    
                    apontamentoService.adicionarApontamento(objeto, apontamento);
                }
            }
    
            EmEtapa emEtapaTarget = new EmEtapa();
            emEtapaTarget.setDevolvido(!acao.getPositivo());
            emEtapaTarget.setEtapa(acao.getProxEtapa());
            emEtapaTarget.setAtividade(acao.getAtividadeFinal());
    
            EmStatus emStatusTarget = new EmStatus();
            emStatusTarget.setStatus(acao.getStatusFinal());
            emStatusTarget.setTimestamp(agora);

            objeto.setEmEtapa(emEtapaTarget);
            objeto.setEmStatus(emStatusTarget);
            return objetoService.save(objeto);
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
