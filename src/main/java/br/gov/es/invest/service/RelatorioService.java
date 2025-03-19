package br.gov.es.invest.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    public Workbook gerarPlanilha(){
        
        
        return new HSSFWorkbook();
    }

}
