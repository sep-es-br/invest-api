/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.invest.utils;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 *
 * @author gean.carneiro
 */
public class FunctionUtils {
    
    
    
    public static String gerarOrderBy(Sort sort) {
        
        
        return sort.map(order -> {
            return (order.getNullHandling() ==  Sort.NullHandling.NULLS_FIRST 
                    ? order.getProperty() + " IS NOT NULL, " 
                    : (order.getNullHandling() == Sort.NullHandling.NULLS_LAST 
                        ? order.getProperty() + " IS NULL" 
                    : "")) 
                    + order.getProperty() + " " + order.getDirection().name();
        }).get().collect(Collectors.joining(", "));
        
    }
    
    
    public static String aplicarPageable(String query, Pageable pageable, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(query);

        if (pageable.getSort().isSorted()) {
            sb.append(" ORDER BY ")
              .append(gerarOrderBy(pageable.getSort()))
              .append("\n");
        }

        if (pageable.isPaged()) {
            params.put("skip", pageable.getOffset());
            params.put("limit", pageable.getPageSize());

            sb.append(""" 
                      SKIP $skip 
                      LIMIT $limit
                      """);
        }

        return sb.toString();
    }
}
