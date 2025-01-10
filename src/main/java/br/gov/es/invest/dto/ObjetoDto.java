package br.gov.es.invest.dto;

import java.util.List;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;

public record ObjetoDto(
    String id,
    String tipoConta,
    String tipo,
    String nome,
    String descricao,
    UnidadeOrcamentariaDTO unidade,
    PlanoOrcamentarioDTO planoOrcamentario,
    LocalidadeDto microregiaoAtendida,
    String infoComplementares,
    Boolean objContratado,
    Boolean audienciaPublica,
    Boolean estrategica,
    Boolean cti,
    Boolean climatica,
    Boolean pip,
    List<CustoDTO> recursosFinanceiros
) {
    
    public ObjetoDto(Objeto model, Conta contaModel) {
        this(
            model.getId(), 
            contaModel instanceof Investimento ? "Investimento" : "Custeio", 
            model.getTipo(), 
            model.getNome(), 
            model.getDescricao(), 
            new UnidadeOrcamentariaDTO(contaModel.getUnidadeOrcamentariaImplementadora()), 
            new PlanoOrcamentarioDTO(contaModel.getPlanoOrcamentarioOrientador()), 
            model.getMicrorregiao() == null ? null : new LocalidadeDto(model.getMicrorregiao()), 
            model.getInfoComplementares(), 
            model.getObjContratado(), 
            model.getAudienciaPublica(), 
            model.getEstrategica(), 
            model.getCti(),
            model.getClimatica(), 
            model.getPip(), 
            model.getCustosEstimadores().stream().map(custo -> new CustoDTO(custo)).sorted((c1, c2) -> c1.getAnoExercicio().compareTo(c2.getAnoExercicio())).toList()
        );
    }

}
