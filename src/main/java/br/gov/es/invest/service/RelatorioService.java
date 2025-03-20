package br.gov.es.invest.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    public Workbook gerarPlanilha(){
        
        

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Base de Dados");

        int rowIndex = 0;
        
        this.createHeaderRow(rowIndex++, sheet);
        
        

        
        return workbook;
    }

    public void createHeaderRow(int index, Sheet sheet) {
        Row row = sheet.createRow(index);

        int colIndex = 0;

        XSSFColor headerColor = getColor(179, 198, 231);

        XSSFCellStyle header1Style = ((XSSFWorkbook)sheet.getWorkbook()).createCellStyle();

        header1Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header1Style.setFillForegroundColor(headerColor);
        header1Style.setBorderBottom(BorderStyle.THIN);
        header1Style.setBorderTop(BorderStyle.THIN);
        header1Style.setBorderLeft(BorderStyle.THIN);
        header1Style.setBorderRight(BorderStyle.THIN);
        header1Style.setAlignment(HorizontalAlignment.CENTER);



        this.createHeaderCell(colIndex++, "Unidade Responsável", header1Style, pixelParaWidth(144) , row);
        this.createHeaderCell(colIndex++, "E-mail do Responsável", header1Style, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Código do PO", header1Style, pixelParaWidth(95), row);
        this.createHeaderCell(colIndex++, "Nome do Projeto/PO", header1Style, pixelParaWidth(450), row);
        this.createHeaderCell(colIndex++, "Descrição/Objeto detalhado", header1Style, pixelParaWidth(450), row);
        this.createHeaderCell(colIndex++, "Tipo de Plano", header1Style, pixelParaWidth(93), row);
        this.createHeaderCell(colIndex++, "Microrregião", header1Style, pixelParaWidth(115), row);
        this.createHeaderCell(colIndex++, "Área Estratégica", header1Style, pixelParaWidth(180), row);
        this.createHeaderCell(colIndex++, "Contrato", header1Style, pixelParaWidth(180), row);
        this.createHeaderCell(colIndex++, "GND", header1Style, pixelParaWidth(50), row);


    }

    public int pixelParaWidth(int pixels) {
        return (int) ((pixels - 5) / 7.0 * 256);
    }

    public void createHeaderCell(int index, String value, XSSFCellStyle style, int largura, Row row) {
        Cell cell = row.createCell(index, CellType.STRING);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        cell.getRow().getSheet().setColumnWidth(index, largura);
    }

    public XSSFColor getColor(int red, int green, int blue){
        byte[] rgb = new byte[]{(byte) red, (byte) green, (byte) blue};
        return new XSSFColor(rgb, null);
    }

}
