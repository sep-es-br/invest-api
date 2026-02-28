/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.feignClient.dto;

/**
 *
 * @author gean.carneiro
 */
public record PropostaResponse(
        String syncHash,
        String proposalText,
        String areaName,
        String budgetUnitId,
        String budgetUnitName,
        String microrregion
    ) {}