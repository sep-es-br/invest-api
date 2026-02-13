package br.gov.es.invest.dto;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.Objeto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public record ObjetoDto(
    Long id,
    String tipoConta,
    String tipo,
    String hashProposta,
    String nome,
    EmStatusDTO emStatus,
    List<EmEtapaDTO> emEtapa, 
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
            model.getHashProposta(),
            model.getNome(), 
            EmStatusDTO.parse(model.getEmStatus()),
            model.getEmEtapa().stream().map(EmEtapaDTO::parse).toList(),
            model.getDescricao(), 
            Optional.ofNullable(model.getMicrorregiao()).map(LocalidadeDto::new).orElse(null), 
            model.getInfoComplementares(), 
            Optional.ofNullable(model.getTiposPlano()).map(list -> list.stream().map(TipoPlanoDto::new).toList()).orElse(null),
            model.getContrato(),
            Optional.ofNullable(model.getAreaTematica()).map(AreaTematicaDto::new).orElse(null),

            Optional.ofNullable(model.getCustosEstimadores()).orElse(new ArrayList<>()).stream()
                    .sorted(Comparator.comparing(Custo::getAnoExercicio)).map(CustoDTO::parse).toList(),

            UsuarioDto.parse(model.getResponsavel()),
            new ContaDto(model.getConta()),
            Optional.ofNullable(model.getApontamentos()).map(list -> list.stream().map(ApontamentoDTO::parse).toList()).orElse(null),
            Optional.ofNullable(model.getPareceres()).map(list -> list.stream().map(ParecerDTO::parse).toList()).orElse(null),
            model.getPossuiOrcamento()
        );
    }


}
