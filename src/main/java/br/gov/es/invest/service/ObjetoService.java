package br.gov.es.invest.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.ObjetoFiltroDTO;
import br.gov.es.invest.dto.projection.ObjetoTiraProjection;
import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.EmEtapa;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Etapa;
import br.gov.es.invest.model.Fluxo;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.PlanoOrcamentario;
import br.gov.es.invest.model.Status;
import br.gov.es.invest.model.StatusEnum;
import br.gov.es.invest.model.TipoPlano;
import br.gov.es.invest.model.UnidadeOrcamentaria;
import br.gov.es.invest.repository.ObjetoRepository;

@Service
public class ObjetoService {
    
    @Autowired
    private ObjetoRepository repository;

    
    private InvestimentoService investimentoService;
    private  UnidadeOrcamentariaService unidadeService;
    private  PlanoOrcamentarioService planoService;
    private  ContaService contaService;
    
    private  StatusService statusService;

    private FluxoService fluxoService;



    public void saveAll(List<Objeto> objetos) {
        repository.saveAll(objetos);
    }

    public Objeto save(Objeto objeto) {
        UnidadeOrcamentaria unidade = unidadeService.findOrCreateByCod(objeto.getConta().getUnidadeOrcamentariaImplementadora());
        
        // define o Investimento que vai ser associado

        // se não tiver PO usa o investimento generico

        Conta conta = null;
        if(objeto.getConta().getPlanoOrcamentario() == null) {
            conta = contaService.getGenericoByCodUnidade(unidade);
        } else { // se não, busca o investimento

            Optional<Investimento> optInvestimento = investimentoService.getByCodUoPo(
                objeto.getConta().getUnidadeOrcamentariaImplementadora().getCodigo(), 
                objeto.getConta().getPlanoOrcamentario().getCodigo()
            );
            Investimento investimento;

            if(optInvestimento.isEmpty()){ // se não existir, cria um novo

                    PlanoOrcamentario plano = planoService.findOrCreateByCod(objeto.getConta().getPlanoOrcamentario());

                    investimento = new Investimento();
                    investimento.setNome(objeto.getNome());
                    investimento.setUnidadeOrcamentariaImplementadora(unidade);
                    investimento.setPlanoOrcamentario(plano);
            } else { // se existir usa o existente
                investimento = optInvestimento.get();
            }
            
            conta = investimento;

        }
        
        objeto.setConta(conta);

        if(objeto.getEmStatus() == null) {
            
            Status novoStatus = statusService.getByStatusId(StatusEnum.SOLICITADO.name()).get();

            EmStatus emStatus = new EmStatus();

            emStatus.setStatus(novoStatus);
            emStatus.setTimestamp(ZonedDateTime.now());

            objeto.setEmStatus(emStatus);

            Fluxo fluxo = fluxoService.findByFluxoId("avaliacaoPip");

            EmEtapa emEtapa = new EmEtapa();
            emEtapa.setAtividade("Avaliar Solicitação");
            emEtapa.setDevolvido(false);
            emEtapa.setEtapa(fluxo.getEtapaInicial());
            
            objeto.setEmEtapa(emEtapa);
            
        }
        
        if(objeto.getId() != null) {
            List<TipoPlano> filhoAtuais = repository.findById(objeto.getId()).get().getTiposPlano();

            List<String> idsFilhoFinal = objeto.getTiposPlano().stream().map( filho -> filho.getId()).toList();

            List<TipoPlano> orfaos = filhoAtuais.stream().filter(filho -> !idsFilhoFinal.contains(filho.getId())).toList();

            if (!orfaos.isEmpty()) {
                repository.removerTipos(orfaos.stream().map(orfao -> orfao.getId()).toList());
            }

        }

        return repository.save(objeto);
    }

    public Objeto findById(String id){
        return repository.findById(id).orElse(null);
    }

    public List<Objeto> getAllListByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String statusId, String fonteId, Pageable pageable){
        List<ObjetoTiraProjection> listTira = Arrays.asList();

        if(pageable != null) {
            listTira = repository.getAllListByFilter(exercicio, nome, idUnidade, idPo, statusId, pageable);
        } else {
            listTira = repository.getAllListByFilter(exercicio, nome, idUnidade, idPo, statusId);
        }

        List<Objeto> objetoFiltrado = listTira.stream().map(Objeto::parse).toList();

        if(statusId != null) {
            objetoFiltrado = objetoFiltrado.stream()
                            .filter( obj -> obj.getEmStatus().getStatus().getId().equals(statusId) )
                            .toList();      
        }

        if(pageable != null)
            objetoFiltrado = objetoFiltrado.subList(Math.toIntExact(pageable.getOffset()) , Math.toIntExact(pageable.getOffset()+Long.min(objetoFiltrado.size(), pageable.getPageSize()) ) );

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(exercicio, fonteId);
        }

        return objetoFiltrado;

    }

    public List<Objeto> getAllListByFilterEmProcessamento(Integer exercicio, String nome, String idUnidade, String idPo, String statusId, String etapaId, String fonteId, Pageable pageable){
        List<ObjetoTiraProjection> listTira = Arrays.asList();

        if(pageable != null) {
            listTira = repository.getAllListByFilter(exercicio, nome, idUnidade, idPo, statusId, pageable);
        } else {
            listTira = repository.getAllListByFilter(exercicio, nome, idUnidade, idPo, statusId);
        }

        List<Objeto> objetoFiltrado = listTira.stream().map(Objeto::parse).filter(obj -> obj.getEmEtapa() != null).toList();

        if(statusId != null) {
            objetoFiltrado = objetoFiltrado.stream()
                            .filter( obj -> obj.getEmStatus().getStatus().getId().equals(statusId) )
                            .toList();      
        }

        if(etapaId != null) {
            objetoFiltrado = objetoFiltrado.stream()
                            .filter( obj -> obj.getEmEtapa() != null && obj.getEmEtapa().getEtapa().getId().equals(etapaId) )
                            .toList();      
        }

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(exercicio, fonteId);
        }

        return objetoFiltrado;

    }

    public List<Objeto> getAllByFilter(Integer exercicio, String nome, String idUnidade, String idPo, String statusId, Pageable pageable) {
        
        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta();
        objetoProbe.setConta(contaProbe);

        if(nome != null) {
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("conta.nome", ExampleMatcher.GenericPropertyMatchers.ignoreCase().contains());
        }

        if(idUnidade != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(idUnidade);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        if(idPo != null) {
            if(idPo.equals("S.PO")) {
                matcher = matcher.withMatcher("conta.planoOrcamentario", ExampleMatcher.GenericPropertyMatchers.exact()).withIncludeNullValues();
            } else {
                PlanoOrcamentario planoProbe = new PlanoOrcamentario();
                planoProbe.setId(idPo);
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        // if(statusId != null) {
        //     Status statusProbe = new Status();
        //     statusProbe.setId(statusId);

        //     EmStatus emStatusProbe = new EmStatus();
        //     emStatusProbe.setStatus(statusProbe);

        //     objetoProbe.setEmStatus(emStatusProbe);


        // }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe));

        if(statusId != null) {
            objetoFiltrado = objetoFiltrado.stream()
                            .filter( obj -> obj.getEmStatus().getStatus().getId().equals(statusId) )
                            .toList();      
        }

        if(pageable != null)
            objetoFiltrado = objetoFiltrado.subList(Math.toIntExact(pageable.getOffset()) , Math.toIntExact(pageable.getOffset()+Long.min(objetoFiltrado.size(), pageable.getPageSize()) ) );

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(exercicio, null);
        }

        return objetoFiltrado;
        
        
    }

    public List<Objeto> findByFilter(
        String nome, String unidadeId, String planoId,
        Integer anoExercicio, String fonteId
    ) {

        ExampleMatcher matcher = ExampleMatcher.matching();
        Objeto objetoProbe = new Objeto();

        Conta contaProbe = new Conta();
        objetoProbe.setConta(contaProbe);

        if(nome != null) {
            contaProbe.setNome(nome);
            matcher = matcher.withMatcher("conta.nome", ExampleMatcher.GenericPropertyMatchers.ignoreCase().contains());
        }

        if(unidadeId != null) {
            UnidadeOrcamentaria unidadeProbe = new UnidadeOrcamentaria();
            unidadeProbe.setId(unidadeId);
            contaProbe.setUnidadeOrcamentariaImplementadora(unidadeProbe);
        }

        if(planoId != null) {
            if(planoId.equals("S.PO")) {
                matcher = matcher.withMatcher("conta.planoOrcamentario", ExampleMatcher.GenericPropertyMatchers.exact()).withIncludeNullValues();
            } else {
                PlanoOrcamentario planoProbe = new PlanoOrcamentario();
                planoProbe.setId(planoId);
                contaProbe.setPlanoOrcamentario(planoProbe);
            }
        }

        List<Objeto> objetoFiltrado = repository.findAll(Example.of(objetoProbe));

        for(Objeto objeto : objetoFiltrado) {
            objeto.filtrar(anoExercicio, fonteId);
        }

        return objetoFiltrado;
    }

    public void updateStatus(String objId, Status novoStatus) {
        Optional<Objeto> optObjeto = repository.findById(objId);
        
        if(optObjeto.isEmpty()) return;

        Objeto obj = optObjeto.get();
        obj.getEmStatus().setStatus(novoStatus);
        obj.getEmStatus().setTimestamp(ZonedDateTime.now());

        repository.save(obj);

    }

    public Objeto getByCusto(Custo custo){
        return repository.getByCusto(custo.getId());
    }

    public Optional<Objeto> getById(String id, boolean updateStatus) {
        Optional<Objeto> optObjeto = repository.findById(id);
        
        if(optObjeto.isPresent() 
            && optObjeto.get().getEmStatus().getStatus().getStatusId().equals(StatusEnum.SOLICITADO) 
            && updateStatus){
            Status novoStatus = statusService.getByStatusId(StatusEnum.EM_ANALISE.name()).get();

            statusService.aplicarStatus(optObjeto.get(), novoStatus);
            optObjeto = repository.findById(id);
        }

        return optObjeto;
    }

    public Optional<Objeto> getById(String id) {
        return this.getById(id, false);
    }

    public List<Objeto> getAllByIds(List<String> ids) {
        return repository.findAllById(ids);
    }

    public int countByFilter(String nome, String codUnidade, String codPO, String status, Integer exercicio) {

        return repository.countByFilter(nome, codUnidade, codPO, status, exercicio);
    }

    public int countByInvestimentoFilter(String nome, String codUnidade, String codPO, Integer exercicio) {
        return repository.countByInvestimentoFilter(nome, codUnidade, codPO, exercicio);
    }

    public List<Status> findStatusCadastrados() {
        return repository.findStatusCadastrados();
    }

    public Objeto removerObjeto(String objetoId) {
        Optional<Objeto> optObjeto = repository.findById(objetoId);

        if(optObjeto.isEmpty())
            return null;

        repository.removerObjeto(objetoId);
        return optObjeto.get();
    }

    public List<Objeto> findObjetoByConta(Conta conta) {
        Objeto objetoProbe = new Objeto();
        Conta contaProbe = new Conta();
        contaProbe.setId(conta.getId());
        objetoProbe.setConta(contaProbe);

        return repository.findAll(Example.of(objetoProbe));
    }

    public List<Objeto> findObjetoByContaFiltrado(Conta conta, Integer exercicio, String fonteId) {
        List<Objeto> todosObjetos = findObjetoByConta(conta);

        for(Objeto objeto : todosObjetos) {
            objeto.filtrar(exercicio, fonteId);
        }

        return todosObjetos;
    }

    @Autowired
    public void setInvestimentoService(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @Autowired
    public void setUnidadeService(UnidadeOrcamentariaService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @Autowired
    public void setPlanoService(PlanoOrcamentarioService planoService) {
        this.planoService = planoService;
    }

    @Autowired
    public void setContaService(ContaService contaService) {
        this.contaService = contaService;
    }

    @Autowired
    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    @Autowired
    public void setEtapaService(FluxoService fluxoService) {
        this.fluxoService = fluxoService;
    }
    

}
