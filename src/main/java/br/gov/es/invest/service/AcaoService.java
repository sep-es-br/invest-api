package br.gov.es.invest.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.invest.exception.SemApontamentosException;
import br.gov.es.invest.model.Apontamento;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Parecer;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.utils.components.FluxoConfig;
import br.gov.es.invest.utils.domains.Acao;
import br.gov.es.invest.utils.domains.Etapa;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcaoService {
    

    private final ApontamentoService apontamentoService;
    private final ObjetoService objetoService;
    private final StatusService statusSrv;
    private final EtapaService etapaSrv;
    private final GrupoService grupoSrv;
    
    private final FluxoConfig fluxoConfig;

    @Transactional
    public Objeto executarAcao(Objeto objeto, List<Apontamento> apontamentos, Parecer parecer, Acao acao, Usuario usuario) throws SemApontamentosException{
        

        if(acao.positivo() != null && apontamentos != null && !acao.positivo() && acao.proxEtapa() != null && apontamentos.isEmpty())
            throw new SemApontamentosException();

        ZonedDateTime agora = ZonedDateTime.now();
        

        Objeto objetoOriginal = objetoService.findById(objeto.getId());
        objeto.setApontamentos(objetoOriginal.getApontamentos());
        objeto.setPareceres(objetoOriginal.getPareceres());

        if(acao.proxEtapa()== null) { // ponta do fluxo
            if(acao.positivo()) { // ação positiva significa que terminou o fluxo
                
                EmStatus emStatusTarget = new EmStatus();
                emStatusTarget.setStatus(statusSrv.getByStatusId(acao.statusFinal()).orElseThrow());
                emStatusTarget.setTimestamp(agora);

                objeto.setEmStatus(emStatusTarget); // aplica status final
                objeto.setEmEtapa(null); // remove objeto do fluxo

                return objetoService.save(objeto);
            } else { // se não significa que o fluxo foi cancelado
                return objetoService.removerObjeto(objeto.getId());
            }

        } else { // meio do fluxo
            Etapa etapa = this.fluxoConfig
                            .getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP)
                            .etapa(objeto.getEmEtapa().getEtapa().getEtapaId().name());
            if(acao.positivo()!= null && !acao.positivo()){         

                if(parecer != null) {
                    
                    

                    parecer.setEtapa(etapaSrv.getByEtapaId(acao.proxEtapa()).orElseThrow());
                    parecer.setGrupo(grupoSrv.findById(etapa.grupoResponsavel()).orElseThrow());
                    parecer.setTimestamp(agora);
                    parecer.setUsuario(usuario);

                    ArrayList<Parecer> todosPareceres = new ArrayList<>(
                        objeto.getPareceres() == null ? Arrays.asList() : objeto.getPareceres()
                    ); 
                    todosPareceres.add(parecer);
                    objeto.setPareceres(todosPareceres);


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
                        
                        Etapa proxEtapa = fluxoConfig
                                .getFluxo(FluxoConfig.FLUXO_AVALIACAO_PIP)
                                .etapa(acao.proxEtapa());
    
                        apontamento.setEtapa(etapaSrv.getByEtapaId(acao.proxEtapa()).orElseThrow() );
                        apontamento.setGrupo(grupoSrv.findById( etapa.grupoResponsavel()).orElseThrow());
                        apontamento.setTimestamp(agora);
                        apontamento.setUsuario(usuario);
                        apontamento.setActive(true);
                        
                    }
    
                    objeto.setApontamentos(apontamentos);
                }

                
            }
            
            EmEtapa emEtapaTarget = new EmEtapa();
            emEtapaTarget.setDevolvido(!acao.positivo());
            emEtapaTarget.setEtapa(etapaSrv.getByEtapaId( acao.proxEtapa() ).orElseThrow());
            emEtapaTarget.setAtividade(acao.atividadeFinal());
            
            objeto.setEmEtapa(emEtapaTarget);
            
             
            EmStatus emStatusTarget = new EmStatus();
            emStatusTarget.setStatus(statusSrv.getByStatusId(acao.statusFinal()).orElseThrow());
            emStatusTarget.setTimestamp(agora);

            objeto.setEmStatus(emStatusTarget);
            objetoService.save(objeto);
            return objetoService.findById(objeto.getId());
        }
        
    }

    

}
