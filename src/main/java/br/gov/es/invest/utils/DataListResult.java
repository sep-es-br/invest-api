package br.gov.es.invest.utils;

import java.util.List;

public record DataListResult<T>(
    List<T> data,
    int ammount
) {
    
}
