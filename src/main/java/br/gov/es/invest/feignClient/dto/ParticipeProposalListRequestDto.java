/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.feignClient.dto;

import lombok.Builder;

/**
 *
 * @author gean.carneiro
 */
@Builder
public record ParticipeProposalListRequestDto(
        String budgetUnitCode,
        Integer year,
        Long planItemId
    ) {}
