/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.feignClient.dto;

import java.util.List;

/**
 *
 * @author gean.carneiro
 */
public record PageResponseDto<T>(
            List<T> pageContent,
            int totalOfElements
        ) {}
