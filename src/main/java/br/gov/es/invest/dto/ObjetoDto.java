package br.gov.es.invest.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import br.gov.es.invest.model.Conta;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.EmStatus;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.Objeto;
import br.gov.es.invest.model.Parecer;

public record ObjetoDto(
    Long id,
    String tipoConta,
    String tipo,
    String nome,
    EmStatusDTO emStatus,
    EmEtapaDTO emEtapa, 
    String descricao,
    LocalidadeDto microregiaoAtendida,
    String infoComplementares,
    List<TipoPlanoDto> planos,
    String contrato,
    AreaTematicaDto areaTematica,
    List<CustoDTO> recursosFinanceiros,
    UsuarioDto responsavel,
    ContaDto conta,
    List<ApontamentoDTO> apontamentos,
    List<ParecerDTO> pareceres,
    String possuiOrcamento

) {
    
    public ObjetoDto(Objeto model) {
        this(
            model.getId(), 
            "Investimento", 
            model.getTipo(), 
            model.getNome(), 
            EmStatusDTO.parse(model.getEmStatus()),
            EmEtapaDTO.parse(model.getEmEtapa()),
            model.getDescricao(), 
            model.getMicrorregiao() == null ? null : new LocalidadeDto(model.getMicrorregiao()), 
            model.getInfoComplementares(), 
            model.getTiposPlano() == null ? null : model.getTiposPlano().stream().map(tipo -> new TipoPlanoDto(tipo)).toList(),
            model.getContrato(),
            model.getAreaTematica() == null ? null : new AreaTematicaDto(model.getAreaTematica()),

            Optional.ofNullable(model.getCustosEstimadores()).orElse(new ArrayList<Custo>()).stream()
                    .sorted(Comparator.comparing(Custo::getAnoExercicio)).map(CustoDTO::parse).toList(),

            UsuarioDto.parse(model.getResponsavel()),
            new ContaDto(model.getConta()),
            model.getApontamentos() == null ? null : model.getApontamentos().stream().map(ApontamentoDTO::parse).toList(),
            model.getPareceres() == null ? null : model.getPareceres().stream().map(ParecerDTO::parse).toList(),
            model.getPossuiOrcamento()
        );
    }


}
