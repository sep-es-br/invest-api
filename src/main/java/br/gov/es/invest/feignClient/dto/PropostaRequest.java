/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.feignClient.dto;

import java.util.List;
import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record PropostaRequest(    
    List<String> budgetUnitCodes,
    String planItemName,
    String textFilter,
    List<String> syncedIds,

    int pageNumber,
    int pageSize
) {}
