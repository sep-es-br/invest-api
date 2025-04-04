package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.dto.RegistroDadoConsolidado;
import br.gov.es.invest.dto.RegistroDadoDetalhado;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorAno;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorFonte;
import br.gov.es.invest.model.FonteOrcamentaria;

@Service
public class RelatorioService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    @Autowired
    private Neo4jOperations neo4jOperations;

    @Autowired
    private FonteOrcamentariaService fonteOrcamentariaService;

    @Autowired
    private InvestimentosBIService investimentosBIService;


    public RegistroDadoConsolidado cardsTotaisRelatorioConsolidado(
        String tipoDespesa, List<String> idsUnidade, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){

        String cypher = "match (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)\r\n" + //
                        "where \r\n" + //
                        "    $tipoDespesa in labels(conta)\r\n" + //
                        "AND ($idsUnidade is null or elementId(unidade) in $idsUnidade)\r\n" + //
                        "AND NOT EXISTS((obj)-[:EM]->(:Etapa))\r\n" + //
                        "CALL(obj) {\r\n" + //
                        "    MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)\r\n" + //
                        "    WHERE ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte)\r\n" + //
                        "        AND ($exercicioInicio <= custo.anoExercicio AND $exercicioFim >= custo.anoExercicio )\r\n" + //
                        "        AND ($gnd IS NULL OR indicada_por.gnd = $gnd)\r\n" + //
                        "    RETURN\r\n" + //
                        "        sum(indicada_por.previsto) AS totalPrevisto,\r\n" + //
                        "        sum(indicada_por.contratado) AS totalContratado\r\n" + //
                        "}\r\n" + //
                        "CALL(conta) {\r\n" + //
                        "    MATCH (inv)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r\n" + //
                        "    WHERE ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)\r\n" + //
                        "        AND ($exercicioInicio <= exec.anoExercicio AND $exercicioFim >= exec.anoExercicio )\r\n" + //
                        "        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)\r\n" + //
                        "    RETURN\r\n" + //
                        "        sum(vinculada_por.autorizado) AS totalAutorizado\r\n" + //
                        "}\r\n" + //
                        "WITH\r\n" + //
                        "    SUM(totalPrevisto) as previsto,\r\n" + //
                        "    SUM(totalContratado) as contratado,\r\n" + //
                        "    totalAutorizado as autorizado\r\n" + //
                        "RETURN\r\n" + //
                        "    SUM(previsto) as previsto,\r\n" + //
                        "    SUM(contratado) as contratado,\r\n" + //
                        "    SUM(autorizado) as autorizado,\r\n" + //
                        "    SUM(autorizado) - SUM(contratado) as difAutorizadoContratado\r\n";
        
        Map<String, Object> params = new HashMap<>();
        params.put("tipoDespesa", tipoDespesa);
        params.put("idsUnidade", idsUnidade);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);
        params.put("exercicioInicio", anoInicio);
        params.put("exercicioFim", anoFim);

        RegistroDadoConsolidado result = neo4jClient.query(cypher)
                                            .bindAll(params)
                                            .fetchAs(RegistroDadoConsolidado.class)
                                            .mappedBy((typeSystem, record) -> RegistroDadoConsolidado.builder()
                                                                                .previsto(record.get("previsto").asDouble())
                                                                                .contratado(record.get("contratado").asDouble())
                                                                                .autorizado(record.get("autorizado").asDouble())
                                                                                .difAutorizadoContratado(record.get("difAutorizadoContratado").asDouble() )
                                                                                .build()
                                                                                )
                                            .first().get();      

        return result;
    }

    public Workbook gerarPlanilha(
        String tipoDespesa, List<String> idsUnidade, List<String> idsPlanos, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){
        
        List<RegistroDadoDetalhado> dados = getRegistroDadoDetalhados(tipoDespesa, idsUnidade, idsPlanos, idFonte, gnd, anoInicio, anoFim);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Base de Dados");

        int rowIndex = 0;
        
        this.createHeaderRow(rowIndex++, dados, sheet);
        this.preencherComDados(rowIndex++, sheet, dados);
                
        return workbook;
    }

    public Workbook gerarPlanilhaConsolidado(
        String tipoDespesa, List<String> idsUnidade, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){
        
        List<RegistroDadoConsolidado> dados = getRegistroDadoConsolidados(tipoDespesa, idsUnidade, idFonte, gnd, anoInicio, anoFim);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Painel - PIP");

        int rowIndex = 0;
        
        this.createHeaderRowConsolidado(rowIndex++, dados, sheet);
        int totalIndex = rowIndex++;

        rowIndex = this.preencherComDadosConsolidado(rowIndex++, sheet, dados);

        this.totalizacaoConsolidado(totalIndex, rowIndex++, sheet);
                
        return workbook;
    }

    private void preencherComDados(int startRow, Sheet sheet, List<RegistroDadoDetalhado> dados){
        int rowIndex = startRow;
        XSSFWorkbook workbook = (XSSFWorkbook)sheet.getWorkbook();

        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        for(RegistroDadoDetalhado registroDadoDetalhado : dados) {

            int colIndex = 0;

            Row row = sheet.createRow(rowIndex++);
            

            this.createCell(colIndex++, registroDadoDetalhado.getUnidadeResponsável(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getNomeResponsavel(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getCodPo(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getNomePo(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getDescObjeto(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getTipoDePlano(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getMicrorregiao(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getAreaEstrategica(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.getContrato(), style, row);
            this.createCell(colIndex++, String.valueOf(registroDadoDetalhado.getGnd()), style, row);

            XSSFCellStyle styleValor = workbook.createCellStyle();
            styleValor.setBorderBottom(BorderStyle.THIN);
            styleValor.setBorderTop(BorderStyle.THIN);
            styleValor.setBorderLeft(BorderStyle.THIN);
            styleValor.setBorderRight(BorderStyle.THIN);
            styleValor.setAlignment(HorizontalAlignment.RIGHT);

            DataFormat format = workbook.createDataFormat();

            styleValor.setDataFormat(format.getFormat("\"R$\" #,##0.00;-\"R$\" #,##0.00;\"-\""));

            for(RegistroDadoDetalhadoValoresPorFonte valoresPorFonte : registroDadoDetalhado.getValoresPorFonte() ) {
                for(RegistroDadoDetalhadoValoresPorAno valoresPorAno : valoresPorFonte.getValoresPorAno()){
                                      
                    this.createCell(colIndex++, valoresPorAno.getPrevisto(), styleValor, row);
                    this.createCell(colIndex++, valoresPorAno.getContratado(), styleValor, row);
                }
            }

        }
    }

    private void createCell(int index, String value, XSSFCellStyle style, Row row){
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        

    }

    private void createCellFormula(int index, String value, XSSFCellStyle style, Row row){
        Cell cell = row.createCell(index, CellType.FORMULA);
        cell.setCellStyle(style);
        cell.setCellFormula(value);
        

    }

    private void createCell(int index, double value, XSSFCellStyle style, Row row){
        Cell cell = row.createCell(index, CellType.NUMERIC);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        

    }

    public void createHeaderRow(int index,List<RegistroDadoDetalhado> dados, Sheet sheet) {
        Row row = sheet.createRow(index);
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();

        int colIndex = 0;

        XSSFColor header1Color = getColor(179, 198, 231);
        XSSFColor header2Color = getColor(222, 235, 246);

        XSSFCellStyle header1Style = workbook.createCellStyle();

        header1Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header1Style.setFillForegroundColor(header1Color);
        header1Style.setBorderBottom(BorderStyle.THIN);
        header1Style.setBorderTop(BorderStyle.THIN);
        header1Style.setBorderLeft(BorderStyle.THIN);
        header1Style.setBorderRight(BorderStyle.THIN);
        header1Style.setAlignment(HorizontalAlignment.CENTER);


        XSSFCellStyle header2Style = workbook.createCellStyle();

        header2Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header2Style.setFillForegroundColor(header2Color);
        header2Style.setBorderBottom(BorderStyle.THIN);
        header2Style.setBorderTop(BorderStyle.THIN);
        header2Style.setBorderLeft(BorderStyle.THIN);
        header2Style.setBorderRight(BorderStyle.THIN);
        header2Style.setAlignment(HorizontalAlignment.CENTER);

        this.createHeaderCell(colIndex++, "Unidade Responsável", header1Style, pixelParaWidth(144) , row);
        this.createHeaderCell(colIndex++, "Nome do Responsável", header1Style, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Código do PO", header1Style, pixelParaWidth(95), row);
        this.createHeaderCell(colIndex++, "Nome do Projeto/PO", header1Style, pixelParaWidth(450), row);
        this.createHeaderCell(colIndex++, "Descrição/Objeto detalhado", header1Style, pixelParaWidth(450), row);
        this.createHeaderCell(colIndex++, "Tipo de Plano", header1Style, pixelParaWidth(100), row);
        this.createHeaderCell(colIndex++, "Microrregião", header1Style, pixelParaWidth(115), row);
        this.createHeaderCell(colIndex++, "Área Estratégica", header1Style, pixelParaWidth(180), row);
        this.createHeaderCell(colIndex++, "Contrato", header1Style, pixelParaWidth(180), row);
        this.createHeaderCell(colIndex++, "GND", header1Style, pixelParaWidth(50), row);

        XSSFCellStyle current = header2Style;
        for(RegistroDadoDetalhadoValoresPorFonte valoresPorFonte :  dados.get(0).getValoresPorFonte()){
            
            String nomeFonte = valoresPorFonte.getFonte().toUpperCase().split(" ").length > 1 
            ? Arrays.asList(valoresPorFonte.getFonte().toUpperCase().split(" ")).stream().map(n -> String.valueOf(n.charAt(0))).collect(Collectors.joining(""))
            : valoresPorFonte.getFonte().toUpperCase();

            for (RegistroDadoDetalhadoValoresPorAno valoresPorAno : valoresPorFonte.getValoresPorAno()){
                
                this.createHeaderCell(colIndex++, "Previsto " + nomeFonte + " " + valoresPorAno.getAno(), current, pixelParaWidth(200), row);
                this.createHeaderCell(colIndex++, "Contratado " + nomeFonte + " " + valoresPorAno.getAno(), current, pixelParaWidth(200), row);
            }

            current = current == header2Style ? header1Style : header2Style;

        }

        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colIndex - 1));


    }

    public void createHeaderRowConsolidado(int index,List<RegistroDadoConsolidado> dados, Sheet sheet) {
        Row row = sheet.createRow(index);
        row.setHeight((short) pixelParaWidth(25));
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();

        int colIndex = 0;

        
        XSSFFont font = workbook.createFont();
        font.setBold(true);

        XSSFCellStyle headerStyleUo = workbook.createCellStyle();

        headerStyleUo.setBorderBottom(BorderStyle.THIN);
        headerStyleUo.setBorderTop(BorderStyle.THIN);
        headerStyleUo.setBorderLeft(BorderStyle.THIN);
        headerStyleUo.setBorderRight(BorderStyle.THIN);
        headerStyleUo.setAlignment(HorizontalAlignment.CENTER);
        headerStyleUo.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyleUo.setWrapText(true);
        headerStyleUo.setFont(font);
        
        XSSFCellStyle headerStylePrevisto = headerStyleUo.copy();
        headerStylePrevisto.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStylePrevisto.setFillForegroundColor(getColor(179, 198, 231));
        
        XSSFCellStyle headerStyleContratado = headerStylePrevisto.copy();
        headerStyleContratado.setFillForegroundColor(getColor(255, 229, 151));
        
        XSSFCellStyle headerStyleAutorizado = headerStylePrevisto.copy();
        headerStyleAutorizado.setFillForegroundColor(getColor(255, 204, 255));
        
        XSSFCellStyle headerStyleEmpenhadoAnt = headerStylePrevisto.copy();
        headerStyleEmpenhadoAnt.setFillForegroundColor(getColor(226, 239, 218));
        
        XSSFCellStyle headerStyleEmpenhado = headerStylePrevisto.copy();
        headerStyleEmpenhado.setFillForegroundColor(getColor(198, 224, 180));
        
        XSSFCellStyle headerStylePago = headerStylePrevisto.copy();
        headerStylePago.setFillForegroundColor(getColor(112, 173, 71));
        
        XSSFCellStyle headerStyleLiquidado = headerStylePrevisto.copy();
        headerStyleLiquidado.setFillForegroundColor(getColor(169, 208, 142));
        
        XSSFCellStyle headerStyleDif = headerStylePrevisto.copy();
        headerStyleDif.setFillForegroundColor(getColor(231, 230, 230));
        
        XSSFCellStyle headerStyleDifAnt = headerStylePrevisto.copy();
        headerStyleDifAnt.setFillForegroundColor(getColor(231, 230, 230));

        this.createHeaderCell(colIndex++, "UO", headerStyleUo, pixelParaWidth(150) , row);
        this.createHeaderCell(colIndex++, "Previsto", headerStylePrevisto, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Contratado", headerStyleContratado, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Autorizado", headerStyleAutorizado, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Empenhado \n (Exercício Anterior)", headerStyleEmpenhadoAnt, pixelParaWidth(200), row);
        this.createHeaderCell(colIndex++, "Empenhado", headerStyleEmpenhado, pixelParaWidth(200), row);
        this.createHeaderCell(colIndex++, "Liquidado", headerStyleLiquidado, pixelParaWidth(200), row);
        this.createHeaderCell(colIndex++, "Pago", headerStylePago, pixelParaWidth(200), row);
        this.createHeaderCell(colIndex++, "Autorizado - Contratado", headerStyleDif, pixelParaWidth(200), row);
        this.createHeaderCell(colIndex++, "Autorizado - Empenhado \n (Ex. Ant.)", headerStyleDifAnt, pixelParaWidth(200), row);

        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colIndex - 1));


    }

    public int pixelParaWidth(int pixels) {
        return (int) ((pixels - 5) / 7.0 * 256);
    }

    public void createHeaderCell(int index, String value, XSSFCellStyle style, int largura, Row row) {
        Cell cell = row.createCell(index, CellType.STRING);
        cell.setCellValue(value);
        if(style != null) cell.setCellStyle(style);
        cell.getRow().getSheet().setColumnWidth(index, largura);
    }

    public XSSFColor getColor(int red, int green, int blue){
        byte[] rgb = new byte[]{(byte) red, (byte) green, (byte) blue};
        return new XSSFColor(rgb, null);
    }

    private List<RegistroDadoDetalhado> getRegistroDadoDetalhados(
        String tipoDespesa, List<String> idsUnidade, List<String> idsPlanos, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim 
        ){
        

        String cypher = """
                        MATCH  
                            (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),
                            (conta)<-[:CUSTEADO]-(obj:Objeto)
                        WHERE  
                            $tipoDespesa IN LABELS(conta)
                            AND NOT EXISTS((obj)-[:EM]->(:Etapa))
                            AND ($unidades IS NULL OR elementId(unidade) IN $unidades)
                            AND ($planos IS NULL OR elementId(po) IN $planos)

                        MATCH (obj)<-[:ESTIMADO]-(:Custo)-[indicada_por:INDICADA_POR]->(fonte:FonteOrcamentaria)
                        WHERE ($fonte IS NULL OR elementId(fonte) = $fonte)
                            AND ($gnd IS NULL OR indicada_por.gnd = $gnd)

                        OPTIONAL MATCH (obj)-[:SOBRE]->(areaTematica:AreaTematica)
                        OPTIONAL MATCH (obj)-[:ATENDE]->(microrregiao:Localidade)
                        OPTIONAL MATCH (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)
                        OPTIONAL MATCH (obj)<-[:RESPONSAVEL_POR]-(usuario:Usuario)

                        WITH 
                            unidade.codigo AS codUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            COALESCE(usuario.nomeCompleto, "-") AS nomeResponsavel,
                            po.codigo AS codPO,
                            po.nome AS nomePO,
                            obj.descricao AS descObjeto,
                            CASE WHEN tipoPlano IS NULL THEN { sigla: 'PIP'} ELSE tipoPlano END AS tiposPo,
                            microrregiao.nome AS microrregiao,
                            areaTematica.nome AS areaTematica,
                            CASE WHEN obj.contrato IS NULL OR obj.contrato = '' THEN '-' ELSE obj.contrato END AS contrato,
                            COALESCE(indicada_por.gnd, -1) AS gnd,
                            elementId(obj) AS objetoId

                        RETURN DISTINCT
                            codUnidade,
                            unidadeResponsavel,
                            nomeResponsavel,
                            codPO,
                            nomePO,
                            descObjeto,
                            apoc.text.join(collect(DISTINCT tiposPo.sigla), '; ') AS tiposPo,
                            COALESCE(microrregiao, ' - ') AS microrregiao,
                            COALESCE(areaTematica, ' - ') AS areaTematica,
                            contrato,
                            gnd,
                            objetoId
                        ORDER BY codUnidade, codPO
                        """;

        String cypherAnos = """
                            MATCH (custo:Custo)
                            WHERE $anoInicio <= custo.anoExercicio <= $anoFim
                            RETURN DISTINCT custo.anoExercicio AS ano
                            ORDER BY ano
                            """;

        Collection<Integer> allAnos = neo4jClient.query(cypherAnos)
                                        .bind(anoInicio).to("anoInicio")
                                        .bind(anoFim).to("anoFim")
                                        .fetchAs(Integer.class).all();

        Collection<FonteOrcamentaria> allFontes = fonteOrcamentariaService.findFontesExtras();
        
        Map<String, Object> params = new HashMap<>();
        params.put("unidades", idsUnidade);
        params.put("planos", idsPlanos);
        params.put("fonte", idFonte);
        params.put("gnd", gnd);
        params.put("tipoDespesa", tipoDespesa);

        String cypherPrevistoContratado = """
                                        MATCH (objeto:Objeto)
                                        WHERE elementId(objeto) = $idObjeto
                                        OPTIONAL MATCH (objeto)<-[:ESTIMADO]-(custo:Custo)
                                        WHERE custo.anoExercicio = $ano
                                        OPTIONAL MATCH (custo)-[indicada_por:INDICADA_POR]->(fonteOrcamentaria:FonteOrcamentaria)\r
                                        WHERE elementId(fonteOrcamentaria) = $idFonte
                                        RETURN COALESCE(indicada_por.previsto, 0) AS previsto,
                                                COALESCE(indicada_por.contratado, 0) AS contratado
                                        """ ;

        Collection<RegistroDadoDetalhado> list = neo4jClient.query(cypher)
                                            .bindAll(params)
                                            .fetchAs(RegistroDadoDetalhado.class)
                                            .mappedBy(((typeSystem, record) -> {

                                                List<RegistroDadoDetalhadoValoresPorFonte> valoresPorFontes = new ArrayList<>();

                                                // montar os valores
                                                for(FonteOrcamentaria fonte : allFontes){
                                                    
                                                    List<RegistroDadoDetalhadoValoresPorAno> valoresPorAno = new ArrayList<>();

                                                    for(Integer ano : allAnos ){

                                                        RegistroDadoDetalhadoValoresPorAno valorPorAno = neo4jClient.query(cypherPrevistoContratado)
                                                                                                            .bindAll(Map.of(
                                                                                                                "idObjeto", record.get("objetoId").asString(),
                                                                                                                "ano", ano,
                                                                                                                "idFonte", fonte.getId()
                                                                                                            )).fetchAs(RegistroDadoDetalhadoValoresPorAno.class)
                                                                                                            .mappedBy((typeSystem2, valores) -> RegistroDadoDetalhadoValoresPorAno.builder()
                                                                                                                                                .ano(ano)
                                                                                                                                                .previsto(valores.get("previsto").asDouble())
                                                                                                                                                .contratado(valores.get("contratado").asDouble())
                                                                                                                                                .build()
                                                                                                            ).first().get();
                                                        valoresPorAno.add(valorPorAno);

                                                    }

                                                    valoresPorFontes.add(RegistroDadoDetalhadoValoresPorFonte.builder()
                                                                            .fonte(fonte.getNome())
                                                                            .valoresPorAno(valoresPorAno)
                                                                            .build());

                                                }

                                                return RegistroDadoDetalhado.builder()
                                                        .unidadeResponsável(record.get("unidadeResponsavel").asString().trim())
                                                        .nomeResponsavel(record.get("nomeResponsavel").asString().trim())
                                                        .codPo(record.get("codPO").asString().trim())
                                                        .nomePo(record.get("nomePO").asString().trim())
                                                        .descObjeto(record.get("descObjeto").asString().trim())
                                                        .tipoDePlano(record.get("tiposPo").asString().trim())
                                                        .microrregiao(record.get("microrregiao").asString().trim())
                                                        .areaEstrategica(record.get("areaTematica").asString().trim())
                                                        .contrato(record.get("contrato").asString().trim())
                                                        .gnd(record.get("gnd").asInt())
                                                        .valoresPorFonte(valoresPorFontes).build();
                                            }))
                                            .all();

        return (List<RegistroDadoDetalhado>) list;

    }

    private List<RegistroDadoConsolidado> getRegistroDadoConsolidados(
        String tipoDespesa, List<String> idsUnidade, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim 
        ){
        
            String cypher = """
                MATCH (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)
                WHERE 
                    $tipoDespesa IN labels(conta)
                    AND ($idsUnidade IS NULL OR elementId(unidade) IN $idsUnidade)
                    AND NOT EXISTS((obj)-[:EM]->(:Etapa))

                // Subconsulta para valores de custo
                CALL (obj) {
                    MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR elementId(fonteCusto) = $idFonte)
                        AND (custo.anoExercicio = $exercicio)
                        AND ($gnd IS NULL OR indicada_por.gnd = $gnd)
                    RETURN
                        SUM(indicada_por.previsto) AS previsto,
                        SUM(indicada_por.contratado) AS contratado
                }

                // Subconsulta para valores de execução
                CALL(conta) {
                    MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)
                        AND (exec.anoExercicio = $exercicio)
                        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                    RETURN
                        SUM(vinculada_por.autorizado) AS autorizado,
                        sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS empenhado,
                        sum(REDUCE(total=0,e IN vinculada_por.liquidado | total + e ))  AS liquidado,
                        sum(REDUCE(total=0,e IN vinculada_por.pago | total + e ))  AS pago
                }

                // Subconsulta para valores de execução do ano anterior
                CALL (conta) {
                    MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR elementId(fonteExec) = $idFonte)
                        AND (exec.anoExercicio = $exercicio - 1)
                        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                    RETURN
                        SUM(vinculada_por.autorizado) AS autorizadoAnt,
                        sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS empenhadoAnt
                }
                RETURN
                    unidade.codigo AS codUnidade,
                    unidade.codigo + ' - ' + unidade.sigla AS unidadeOrcamentaria,
                    COALESCE(SUM(previsto), 0) AS previsto,
                    COALESCE(SUM(contratado), 0) AS contratado,
                    COALESCE(SUM(autorizado), 0) AS autorizado,
                    COALESCE(SUM(empenhadoAnt), 0) AS empenhadoAnt,
                    COALESCE(SUM(empenhado), 0) AS empenhado,
                    COALESCE(SUM(liquidado), 0) AS liquidado,
                    COALESCE(SUM(pago), 0) AS pago,
                    COALESCE(SUM(autorizado), 0) - COALESCE(SUM(contratado), 0) AS difAutorizadoContratado,
                    COALESCE(SUM(autorizadoAnt), 0) - COALESCE(SUM(empenhadoAnt), 0) AS difAutorizadoEmpenhadoAnt
                ORDER BY codUnidade
                """;
        
        Map<String, Object> params = new HashMap<>();
        params.put("idsUnidade", idsUnidade);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);
        params.put("tipoDespesa", tipoDespesa);
        params.put("exercicio", anoInicio);


        List<RegistroDadoConsolidado> list = (List<RegistroDadoConsolidado>) neo4jClient.query(cypher)
                    .bindAll(params)
                    .fetchAs(RegistroDadoConsolidado.class)
                    .mappedBy((typeSystem, record) -> {
                        
                        Map<String, JsonNode> exec = investimentosBIService.getCardsTotais(null, anoInicio, record.get("codUnidade").asString(), null, null).get(0);
                        Map<String, JsonNode> execAnt = investimentosBIService.getCardsTotais(null, anoInicio-1, record.get("codUnidade").asString(), null, null).get(0);

                        return RegistroDadoConsolidado.builder()
                        .unidadeOrcamentaria(record.get("unidadeOrcamentaria").asString())
                        .previsto(record.get("previsto").asDouble())
                        .contratado(record.get("contratado").asDouble())
                        .autorizado(exec.get("autorizado").asDouble())
                        .empenhadoAnt(execAnt.get("empenhado").asDouble())
                        .empenhado(exec.get("empenhado").asDouble())
                        .liquidado(exec.get("liquidado").asDouble())
                        .pago(exec.get("pago").asDouble())
                        .build();
                    }).all();

        return list;

    }

    // totaisCusto.previsto(), 
    // totaisCusto.contratado(), 
    // linhaResultado.get("orcado").asDouble(), 
    // linhaResultado.get("autorizado").asDouble(), 
    // linhaResultado.get("empenhado").asDouble(), 
    // linhaResultado.get("liquidado").asDouble(), 
    // linhaResultado.get("disponivel_sem_reserva").asDouble(), 
    // linhaResultado.get("pago").asDouble()
    // ));

    private void totalizacaoConsolidado(int indexTotal, int ultIndex, Sheet sheet) {
        
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();

        XSSFFont font = workbook.createFont();
        font.setBold(true);


        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(getColor(174, 170, 170));

        
        XSSFCellStyle styleValor = style.copy();
        styleValor.setAlignment(HorizontalAlignment.RIGHT);

        DataFormat format = workbook.createDataFormat();

        styleValor.setDataFormat(format.getFormat("\"R$\" #,##0.00;-\"R$\" #,##0.00;\"-\""));

        Row row = sheet.createRow(indexTotal);

        int colIndex = 0;
        Function<Integer, String> gerarSum = col -> {
            String letraCol = CellReference.convertNumToColString(col);
            return String.format("SUM(%s%d:%s%d)",letraCol, indexTotal+2, letraCol, ultIndex);
        };

        this.createCell(colIndex++, "Total", style, row);
        for(int col = colIndex; col <= 9; col++){
            this.createCellFormula(col, gerarSum.apply(col), styleValor, row);
        }


    }

    private int preencherComDadosConsolidado(int indexInicial, Sheet sheet, List<RegistroDadoConsolidado> dados) {
        
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();

        int rowIndex = indexInicial;

        XSSFCellStyle styleClaro = workbook.createCellStyle();
        styleClaro.setBorderBottom(BorderStyle.THIN);
        styleClaro.setBorderTop(BorderStyle.THIN);
        styleClaro.setBorderLeft(BorderStyle.THIN);
        styleClaro.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle styleEscuro = styleClaro.copy();
        styleEscuro.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleEscuro.setFillForegroundColor(getColor(217, 217, 217));

        int colIndex = 0;

        for(RegistroDadoConsolidado registroDadoDetalhado : dados) {

            colIndex = 0;

            Row row = sheet.createRow(rowIndex++);    
            
            XSSFCellStyle style = (rowIndex-1) % 2 == 0 ? styleClaro : styleEscuro;
            
            XSSFCellStyle styleValor = style.copy();
            styleValor.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat format = workbook.createDataFormat();
    
            styleValor.setDataFormat(format.getFormat("\"R$\" #,##0.00;-\"R$\" #,##0.00;\"-\""));

            this.createCell(colIndex++, registroDadoDetalhado.unidadeOrcamentaria(), style, row);
            this.createCell(colIndex++, registroDadoDetalhado.previsto(), styleValor, row);
            String contratadoRef = String.format("%s%d", CellReference.convertNumToColString(colIndex), rowIndex);
            this.createCell(colIndex++, registroDadoDetalhado.contratado(), styleValor, row);
            String autorizadoRef = String.format("%s%d", CellReference.convertNumToColString(colIndex), rowIndex);
            this.createCell(colIndex++, registroDadoDetalhado.autorizado(), styleValor, row);
            String empenhadoAntRef = String.format("%s%d", CellReference.convertNumToColString(colIndex), rowIndex);
            this.createCell(colIndex++, registroDadoDetalhado.empenhadoAnt(), styleValor, row);
            this.createCell(colIndex++, registroDadoDetalhado.empenhado(), styleValor, row);
            this.createCell(colIndex++, registroDadoDetalhado.liquidado(), styleValor, row);
            this.createCell(colIndex++, registroDadoDetalhado.pago(), styleValor, row);
            this.createCellFormula(colIndex++, String.format("%s - %s", autorizadoRef, contratadoRef) , styleValor, row);
            this.createCellFormula(colIndex++, String.format("%s - %s", autorizadoRef, empenhadoAntRef) , styleValor, row);
            
        }

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule negStroke = sheetCF.createConditionalFormattingRule(ComparisonOperator.LT, "0");

        FontFormatting stroke = negStroke.createFontFormatting();
        stroke.setFontStyle(true, true);

        ConditionalFormattingRule scaleRule = sheetCF.createConditionalFormattingColorScaleRule();
        ColorScaleFormatting colorScale = scaleRule.getColorScaleFormatting();

        colorScale.setColors(new XSSFColor[]{
            getColor(248, 105, 107),
            getColor(255, 235, 132),
            getColor(99, 190, 123)
        });

        ConditionalFormattingThreshold[] thresholds = new ConditionalFormattingThreshold[3];

        thresholds[0] = colorScale.createThreshold();
        thresholds[0].setRangeType(ConditionalFormattingThreshold.RangeType.MIN);

        thresholds[1] = colorScale.createThreshold();
        thresholds[1].setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER);
        thresholds[1].setValue(0d);

        thresholds[2] = colorScale.createThreshold();
        thresholds[2].setRangeType(ConditionalFormattingThreshold.RangeType.MAX);

        colorScale.setThresholds(thresholds);

        CellRangeAddress[] range = new CellRangeAddress[]{ new CellRangeAddress(indexInicial, rowIndex - 1, colIndex-2, colIndex - 1)};

        sheetCF.addConditionalFormatting(range, negStroke);

        sheetCF.addConditionalFormatting(range, scaleRule);


        return rowIndex;

    }

}
