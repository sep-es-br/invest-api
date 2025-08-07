package br.gov.es.invest.utils;

import br.gov.es.invest.feignClient.dto.PageResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;

public record DataListResult<T>(
    List<T> data,
    int ammount
) {
    public DataListResult(Page<T> page) {
        this(page.getContent(), (int)page.getTotalElements());
    }
    
    public DataListResult(PageResponseDto<T> page) {
        this(page.pageContent(), page.totalOfElements());
    }
}
