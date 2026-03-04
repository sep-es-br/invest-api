package br.gov.es.invest.dto;

import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.Objeto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public record ObjetoDto(
    Long id,
    Integer gnd,
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
    


}
