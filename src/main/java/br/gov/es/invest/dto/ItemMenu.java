package br.gov.es.invest.dto;

import java.util.List;

public record ItemMenu(
    String titulo,
    String icone,
    Boolean ativo,
    String link,
    List<ItemMenu> subItens
) {
}