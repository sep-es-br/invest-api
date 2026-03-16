/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.utils;

import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;

/**
 *
 * @author gean.carneiro
 */
public class FunctionUtils {
    
    public static String gerarOrderBy(Sort sort) {
        
        
        return sort.map(order -> {
            return order.getProperty() + " " + order.getDirection().name();
        }).get().collect(Collectors.joining(","));
        
    }
}
