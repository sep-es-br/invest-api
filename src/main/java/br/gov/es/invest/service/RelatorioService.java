package br.gov.es.invest.service;

import br.gov.es.invest.dto.RegistroDadoConsolidado;
import br.gov.es.invest.dto.RegistroDadoDetalhado;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorAno;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorFonte;
import br.gov.es.invest.model.FonteOrcamentaria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFills;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STGradientType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    @Autowired
    private Neo4jOperations neo4jOperations;

    @Autowired
    private FonteOrcamentariaService fonteOrcamentariaService;

    @Autowired
    private UnidadeOrcamentariaService unidadeOrcamentariaService;

    @Autowired
    private InvestimentosBIService investimentosBIService;
    
    @Autowired
    private ObjectMapper objMapper;

    public RegistroDadoConsolidado cardsTotaisRelatorioConsolidado(
        String tipoDespesa, List<Long> idsUnidade, Long idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){

        String cypher = """
                        match (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(:Status{statusId: 'CADASTRADO'})
                        where 
                            $tipoDespesa in labels(conta)
                        AND ($idsUnidade is null or id(unidade) in $idsUnidade)
                        CALL(obj) {
                            MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                            WHERE ($idFonte IS NULL OR id(fonteCusto) = $idFonte)
                                AND ($exercicioInicio <= custo.anoExercicio AND $exercicioFim >= custo.anoExercicio )
                                AND ($gnd IS NULL OR obj.gnd = $gnd)
                            RETURN
                                sum(indicada_por.planejado) AS totalPlanejado,
                                sum(indicada_por.contratado) AS totalContratado
                        }\r
                        CALL(conta) {
                            MATCH (inv)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)\r
                            WHERE ($idFonte IS NULL OR id(fonteExec) = $idFonte)
                                AND ($exercicioInicio <= exec.anoExercicio AND $exercicioFim >= exec.anoExercicio )
                                AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                            RETURN
                                sum(vinculada_por.autorizado) AS totalAutorizado
                        }\r
                        WITH
                            SUM(totalPlanejado) as planejado,
                            SUM(totalContratado) as contratado,
                            totalAutorizado as autorizado
                        RETURN {
                            SUM(planejado) as planejado,
                            SUM(contratado) as contratado,
                            SUM(autorizado) as autorizado,
                            SUM(autorizado) - SUM(contratado) as difAutorizadoContratado
                        }
                        """;
        
        Map<String, Object> params = new HashMap<>();
        params.put("tipoDespesa", tipoDespesa);
        params.put("idsUnidade", idsUnidade);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);
        params.put("exercicioInicio", anoInicio);
        params.put("exercicioFim", anoFim);

        String codFonte = fonteOrcamentariaService.findById(idFonte).map(FonteOrcamentaria::getCodigo).orElse(null);
        List<String> UoCods = Optional.ofNullable(idsUnidade).map(unidadeOrcamentariaService::getCodsByIds).orElse(null);
        String codsList = Optional.ofNullable(UoCods).map(cods -> String.join(", ", cods) ).orElse(null);
        Map<String, JsonNode> exec = investimentosBIService.getCardsTotais(codFonte, anoInicio, codsList, null, gnd).get(0);

        RegistroDadoConsolidado result = neo4jClient.query(cypher)
                                            .bindAll(params)
                                            .fetchAs(RegistroDadoConsolidado.class)
                                            .mappedBy((typeSystem, record) -> 
                                                    objMapper.convertValue(record.get(0).asMap(), RegistroDadoConsolidado.class)
                                            ).first().get();      

        return result;
    }

    public Workbook gerarPlanilha(
        String tipoDespesa, List<Long> idsUnidade, List<Long> idsPlanos, Long idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){
        
        List<RegistroDadoDetalhado> dados = getRegistroDadoDetalhados(tipoDespesa, idsUnidade, idsPlanos, idFonte, gnd, anoInicio, anoFim);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Base de Dados");

        int rowIndex = 0;
        
        this.createHeaderRow(rowIndex++, dados, sheet);
        int totalIndex = rowIndex++;
        int[] lastPos = this.preencherComDados(rowIndex++, sheet, dados);
        
        this.totalizacaoDetalhado(totalIndex, lastPos[0], lastPos[1], sheet);
        
        ZonedDateTime agora = ZonedDateTime.now();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' hh:mm a", new Locale("pt", "BR"));
        
        XSSFCellStyle style = workbook.createCellStyle();
        style.getFont().setItalic(true);
        
        this.createCell(0, "Extraido em " + agora.format(formatter), style, sheet.createRow(lastPos[0]));
                
        return workbook;
    }

    public Workbook gerarPlanilhaConsolidado(
        String tipoDespesa, List<Long> idsUnidade, Long idFonte, Integer gnd, Integer anoInicio, Integer anoFim
    ){
        
        List<RegistroDadoConsolidado> dados = getRegistroDadoConsolidados(tipoDespesa, idsUnidade, idFonte, gnd, anoInicio, anoFim);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Painel - PIP");

        int rowIndex = 0;
        
        this.createHeaderRowConsolidado(rowIndex++, dados, sheet);
        sheet.createFreezePane(0, 1);
        int totalIndex = rowIndex++;

        rowIndex = this.preencherComDadosConsolidado(rowIndex++, sheet, dados);

        this.totalizacaoConsolidado(totalIndex, rowIndex, sheet);
        
        ZonedDateTime agora = ZonedDateTime.now();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' hh:mm a", new Locale("pt", "BR"));
        
        XSSFCellStyle style = workbook.createCellStyle();
        style.getFont().setItalic(true);
        
        this.createCell(0, "Extraido em " + agora.format(formatter), style, sheet.createRow(rowIndex++));
                
        return workbook;
    }

    private int[] preencherComDados(int startRow, Sheet sheet, List<RegistroDadoDetalhado> dados){
        int rowIndex = startRow;
        XSSFWorkbook workbook = (XSSFWorkbook)sheet.getWorkbook();

        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        int colIndex = 0;
        for(RegistroDadoDetalhado registroDadoDetalhado : dados) {

            colIndex = 0;

            Row row = sheet.createRow(rowIndex++);
            

            this.createCell(colIndex++, registroDadoDetalhado.getUnidadeResponsavel(), style, row);
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
                                      
                    this.createCell(colIndex++, valoresPorAno.getPlanejado(), styleValor, row);
                    this.createCell(colIndex++, valoresPorAno.getContratado(), styleValor, row);
                }
            }

        }
        
        return new int[]{rowIndex, colIndex};
    }

    private Cell createCell(int index, String value, XSSFCellStyle style, Row row){
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        
        return cell;
        

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
                
                this.createHeaderCell(colIndex++, "Planejado " + nomeFonte + " " + valoresPorAno.getAno(), current, pixelParaWidth(200), row);
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
        
        XSSFCellStyle headerStylePlanejado = headerStyleUo.copy();
        headerStylePlanejado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStylePlanejado.setFillForegroundColor(getColor(179, 198, 231));
        
        XSSFCellStyle headerStyleContratado = headerStylePlanejado.copy();
        headerStyleContratado.setFillForegroundColor(getColor(255, 229, 151));
        
        XSSFCellStyle headerStyleOrcado = headerStylePlanejado.copy();
        headerStyleOrcado.setFillForegroundColor(getColor(255, 204, 102));
        
        XSSFCellStyle headerStyleAutorizado = headerStylePlanejado.copy();
        headerStyleAutorizado.setFillForegroundColor(getColor(255, 204, 255));
        
        XSSFCellStyle headerStyleEmpenhadoAnt = headerStylePlanejado.copy();
        headerStyleEmpenhadoAnt.setFillForegroundColor(getColor(226, 239, 218));
        
        XSSFCellStyle headerStyleEmpenhado = headerStylePlanejado.copy();
        headerStyleEmpenhado.setFillForegroundColor(getColor(198, 224, 180));
        
        XSSFCellStyle headerStylePago = headerStylePlanejado.copy();
        headerStylePago.setFillForegroundColor(getColor(112, 173, 71));
        
        XSSFCellStyle headerStyleLiquidado = headerStylePlanejado.copy();
        headerStyleLiquidado.setFillForegroundColor(getColor(169, 208, 142));
        
        XSSFCellStyle headerStyleDif = headerStylePlanejado.copy();
        headerStyleDif.setFillForegroundColor(getColor(231, 230, 230));
        
        XSSFCellStyle headerStyleDifAnt = headerStylePlanejado.copy();
        headerStyleDifAnt.setFillForegroundColor(getColor(231, 230, 230));

        this.createHeaderCell(colIndex++, "UO", headerStyleUo, pixelParaWidth(150) , row);
        this.createHeaderCell(colIndex++, "Planejado", headerStylePlanejado, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Contratado", headerStyleContratado, pixelParaWidth(150), row);
        this.createHeaderCell(colIndex++, "Orçado", headerStyleOrcado, pixelParaWidth(150), row);
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
        String tipoDespesa, List<Long> idsUnidade, List<Long> idsPlanos, Long idFonte, Integer gnd, Integer anoInicio, Integer anoFim 
        ){
        

        String cypher = """
                        MATCH 
                            (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),
                            (conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(:Status {statusId:'CADASTRADO'})
                        WHERE  
                            $tipoDespesa IN labels(conta)
                            AND ($unidades IS NULL OR id(unidade) IN $unidades)
                            AND ($planos IS NULL OR id(po) IN $planos)
                        
                        MATCH (obj)<-[:ESTIMADO]-(:Custo)-[indicada_por:INDICADA_POR]->(fonteFiltro:FonteOrcamentaria)
                        WHERE ($fonte IS NULL OR id(fonteFiltro) = $fonte)
                            AND ($gnd IS NULL OR obj.gnd = $gnd)
                        
                        OPTIONAL MATCH (obj)-[:SOBRE]->(areaTematica:AreaTematica)
                        OPTIONAL MATCH (obj)-[:ATENDE]->(microrregiao:Localidade)
                        OPTIONAL MATCH (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)
                        OPTIONAL MATCH (obj)<-[:RESPONSAVEL_POR]-(usuario:Agente)
                        
                        WITH 
                            unidade.codigo AS codUnidade,
                            unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,
                            COALESCE(usuario.nomeCompleto, "-") AS nomeResponsavel,
                            po.codigo AS codPo,
                            po.nome AS nomePo,
                            obj,
                            obj.descricao AS descObjeto,
                            CASE WHEN tipoPlano IS NULL THEN 'PIP' ELSE tipoPlano.sigla END AS tipoPo,
                            microrregiao.nome AS microrregiao,
                            areaTematica.nome AS areaTematica,
                            CASE WHEN obj.contrato IS NULL OR obj.contrato = '' THEN '-' ELSE obj.contrato END AS contrato,
                            COALESCE(indicada_por.gnd,-1) AS gnd
                        
                        CALL (obj){
                            MATCH (fonte:FonteOrcamentaria)
                            WHERE toInteger(fonte.codigo) < 10000
                                AND ($fonte IS NULL OR id(fonte) = $fonte)
                        
                            WITH obj, fonte, range($anoInicio, $anoFim) AS anos
                        
                            UNWIND anos AS ano
                        
                            OPTIONAL MATCH (obj)<-[:ESTIMADO]-(c:Custo {anoExercicio: ano})
                            OPTIONAL MATCH (c)-[ip:INDICADA_POR]->(fonte)
                        
                            WITH 
                                fonte.nome AS fonteNome,
                                ano,
                                COALESCE(ip.planejado,0) AS planejado,
                                COALESCE(ip.contratado,0) AS contratado
                        
                            WITH 
                                fonteNome,
                                collect({
                                    ano: ano,
                                    planejado: planejado,
                                    contratado: contratado
                                }) AS valoresPorAno
                        
                            RETURN collect({
                                fonte: fonteNome,
                                valoresPorAno: valoresPorAno
                            }) AS valoresPorFonte
                        }
                        
                        RETURN DISTINCT
                            codUnidade,
                            unidadeResponsavel,
                            nomeResponsavel,
                            codPo,
                            nomePo,
                            descObjeto,
                            apoc.text.join(collect(DISTINCT tipoPo), '; ') AS tipoDePlano,
                            COALESCE(microrregiao,' - ') AS microrregiao,
                            COALESCE(areaTematica,' - ') AS areaEstrategica,
                            contrato,
                            gnd,
                            valoresPorFonte
                        
                        ORDER BY codUnidade, codPO
                        """;

        Map<String, Object> params = new HashMap<>();
        params.put("unidades", idsUnidade);
        params.put("planos", idsPlanos);
        params.put("fonte", idFonte);
        params.put("gnd", gnd);
        params.put("tipoDespesa", tipoDespesa);
        params.put("anoInicio", anoInicio);
        params.put("anoFim", anoFim);

        Collection<RegistroDadoDetalhado> list = neo4jClient.query(cypher)
                                            .bindAll(params)
                                            .fetchAs(RegistroDadoDetalhado.class)
                                            .mappedBy((typeSystem, record) -> 
                                                    objMapper.convertValue(record.asMap(), RegistroDadoDetalhado.class)
                                            ).all();

        return (List<RegistroDadoDetalhado>) list;

    }

    private List<RegistroDadoConsolidado> getRegistroDadoConsolidados(
        String tipoDespesa, List<Long> idsUnidade, Long idFonte, Integer gnd, Integer anoInicio, Integer anoFim 
        ){
        
            String cypher = """
                MATCH (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(:Status{statusId: 'CADASTRADO'})
                WHERE 
                    $tipoDespesa IN labels(conta)
                    AND ($idsUnidade IS NULL OR id(unidade) IN $idsUnidade)

                // Subconsulta para valores de custo
                CALL (obj) {
                    MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR id(fonteCusto) = $idFonte)
                        AND (custo.anoExercicio = $exercicio)
                        AND ($gnd IS NULL OR obj.gnd = $gnd)
                    RETURN
                        SUM(indicada_por.planejado) AS planejado,
                        SUM(indicada_por.contratado) AS contratado
                }

                // Subconsulta para valores de execução
                CALL(conta) {
                    MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR id(fonteExec) = $idFonte)
                        AND (exec.anoExercicio = $exercicio)
                        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                    RETURN
                        SUM(vinculada_por.autorizado) AS autorizado,
                        SUM(vinculada_por.orcado) AS orcado,
                        sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS empenhado,
                        sum(REDUCE(total=0,e IN vinculada_por.liquidado | total + e ))  AS liquidado,
                        sum(REDUCE(total=0,e IN vinculada_por.pago | total + e ))  AS pago
                }

                // Subconsulta para valores de execução do ano anterior
                CALL (conta) {
                    MATCH (conta)<-[:DELIMITA]-(exec:ExecucaoOrcamentaria)-[vinculada_por:VINCULADA_POR]->(fonteExec:FonteOrcamentaria)
                    WHERE 
                        ($idFonte IS NULL OR id(fonteExec) = $idFonte)
                        AND (exec.anoExercicio = $exercicio - 1)
                        AND ($gnd IS NULL OR vinculada_por.gnd = $gnd)
                    RETURN
                        SUM(vinculada_por.autorizado) AS autorizadoAnt,
                        sum(REDUCE(total=0,e IN vinculada_por.empenhado | total + e ))  AS empenhadoAnt
                }
                RETURN
                    unidade.codigo AS codUnidade,
                    unidade.codigo + ' - ' + unidade.sigla AS unidadeOrcamentaria,
                    COALESCE(SUM(planejado), 0) AS planejado,
                    COALESCE(SUM(contratado), 0) AS contratado,
                    COALESCE(SUM(orcado), 0) AS orcado,
                    COALESCE(SUM(autorizado), 0) AS autorizado,
                    COALESCE(SUM(empenhadoAnt), 0) AS empenhadoAnt,
                    COALESCE(SUM(empenhado), 0) AS empenhado,
                    COALESCE(SUM(liquidado), 0) AS liquidado,
                    COALESCE(SUM(pago), 0) AS pago
                ORDER BY codUnidade
                """;
        
        Map<String, Object> params = new HashMap<>();
        params.put("idsUnidade", idsUnidade);
        params.put("idFonte", idFonte);
        params.put("gnd", gnd);
        params.put("tipoDespesa", tipoDespesa);
        params.put("exercicio", anoInicio);


        String codFonte = fonteOrcamentariaService.findById(idFonte).map(FonteOrcamentaria::getCodigo).orElse(null);
        List<RegistroDadoConsolidado> list = (List<RegistroDadoConsolidado>) neo4jClient.query(cypher)
                    .bindAll(params)
                    .fetchAs(RegistroDadoConsolidado.class)
                    .mappedBy((typeSystem, record) -> {
                        

                        Map<String, JsonNode> exec = investimentosBIService.getCardsTotais(codFonte, anoInicio, record.get("codUnidade").asString(), null, gnd).get(0);
                        Map<String, JsonNode> execAnt = investimentosBIService.getCardsTotais(codFonte, anoInicio-1, record.get("codUnidade").asString(), null, gnd).get(0);

                        return RegistroDadoConsolidado.builder()
                        .unidadeOrcamentaria(record.get("unidadeOrcamentaria").asString())
                        .planejado(record.get("planejado").asDouble())
                        .contratado(record.get("contratado").asDouble())
                        .autorizado(exec.get("autorizado").asDouble())
                        .orcado(exec.get("orcado").asDouble())
                        .empenhadoAnt(execAnt.get("empenhado").asDouble())
                        .empenhado(exec.get("empenhado").asDouble())
                        .liquidado(exec.get("liquidado").asDouble())
                        .pago(exec.get("pago").asDouble())
                        .build();
                    }).all();

        return list;

    }

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
        for(int col = colIndex; col <= 10; col++){
            this.createCellFormula(col, gerarSum.apply(col), styleValor, row);
        }


    }

    private void totalizacaoDetalhado(int indexTotal, int ultIndex, int ultCol, Sheet sheet) {
        
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
        for(int col = 0; col < 9; col++) this.createCell(colIndex++, "", style, row);
        for(int col = colIndex; col <= ultCol-1; col++){
            this.createCellFormula(col, gerarSum.apply(col), styleValor, row);
        }


    }

    private int preencherComDadosConsolidado(int indexInicial, Sheet sheet, List<RegistroDadoConsolidado> dados) {
        
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();

        int rowIndex = indexInicial;

        XSSFCellStyle styleClaro = createNewCellStyle(workbook);

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
            this.createCell(colIndex++, registroDadoDetalhado.planejado(), styleValor, row);
            String contratadoRef = String.format("%s%d", CellReference.convertNumToColString(colIndex), rowIndex);
            this.createCell(colIndex++, registroDadoDetalhado.contratado(), styleValor, row);
            this.createCell(colIndex++, registroDadoDetalhado.orcado(), styleValor, row);
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
        
        
        XSSFColor vermelho = getColor(248, 105, 107);
        XSSFColor amarelo = getColor(255, 235, 132);
        XSSFColor verde = getColor(99, 190, 123);
        
        

        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule negStroke = sheetCF.createConditionalFormattingRule(ComparisonOperator.LT, "0");

        FontFormatting stroke = negStroke.createFontFormatting();
        stroke.setFontStyle(true, true);

        ConditionalFormattingRule scaleRule = sheetCF.createConditionalFormattingColorScaleRule();
        ColorScaleFormatting colorScale = scaleRule.getColorScaleFormatting();

        colorScale.setColors(new XSSFColor[]{
            vermelho,
            amarelo,
            verde
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
        
        
        colIndex++;
        int rowIndexLegenda = 4;
        Row row = sheet.getRow(rowIndexLegenda++);
        int colWidth = 125;
        row.getSheet().setColumnWidth(colIndex, pixelParaWidth(colWidth));        
        
        XSSFFont negrito = workbook.createFont();
        negrito.setBold(true);
        
        // Vermelho → Amarelo
        XSSFCellStyle redCell = criarEstiloGradiente(workbook, negrito,
                new XSSFColor[]{vermelho, lerpColor(0.66, vermelho, amarelo)},
                new double[]{0.0, 1.0});
        this.createCell(colIndex++, "Menores valores", redCell, row);

        row.getSheet().setColumnWidth(colIndex, pixelParaWidth(colWidth));        

        // Vermelho → Amarelo → Verde
        XSSFCellStyle yellowCell = criarEstiloGradiente(workbook, negrito,
                new XSSFColor[]{lerpColor(0.66, vermelho, amarelo), amarelo, lerpColor(0.33, amarelo, verde)},
                new double[]{0.0, 0.5, 1.0});
        this.createCell(colIndex++, "Intermediários", yellowCell, row);
        yellowCell.setAlignment(HorizontalAlignment.CENTER);
        row.getSheet().setColumnWidth(colIndex, pixelParaWidth(colWidth));        

        // Amarelo → Verde
        XSSFCellStyle greenCell = criarEstiloGradiente(workbook, negrito,
                new XSSFColor[]{lerpColor(0.33, amarelo, verde), verde},
                new double[]{0.0, 1.0});
        greenCell.setAlignment(HorizontalAlignment.RIGHT);
        this.createCell(colIndex++, "Maiores valores", greenCell, row);
        StylesTable styles = workbook.getStylesSource();
        
        return rowIndex;

    }
    
    private XSSFCellStyle criarEstiloGradiente(XSSFWorkbook workbook, XSSFFont font, XSSFColor[] cores, double[] posicoes) {
        try {
            
            // 1. Novo estilo
            XSSFCellStyle style = createNewCellStyle(workbook);
            if (font != null) style.setFont(font);

            // 2. Novo CTFill e gradiente
            CTFill ctFill = CTFill.Factory.newInstance();
            CTGradientFill gradFill = ctFill.addNewGradientFill();
            gradFill.setType(STGradientType.LINEAR);
            gradFill.setDegree(0);

            for (int i = 0; i < cores.length; i++) {
                CTGradientStop stop = gradFill.addNewStop();
                stop.setPosition(posicoes[i]);

                stop.addNewColor().setRgb(cores[i].getRGB()); // novo CTColor para cada stop
            }

            // 3. Registrar fill único
            IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

            XSSFCellFill xssfFill = new XSSFCellFill(ctFill, colorMap); 
            StylesTable styles = workbook.getStylesSource();

            Field fillsField = StylesTable.class.getDeclaredField("fills");
            fillsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<XSSFCellFill> internalFills =
                    (List<XSSFCellFill>) fillsField.get(styles);

            internalFills.add(xssfFill);

            int fillIndex = internalFills.size() - 1;


            CTStylesheet ct = styles.getCTStylesheet();

            CTFills ctFills = ct.getFills();
            if (ctFills == null) {
                ctFills = ct.addNewFills();
            }

            ctFills.addNewFill().set(ctFill);
            ctFills.setCount(ctFills.sizeOfFillArray());

            style.getCoreXf().setFillId(fillIndex);
            style.getCoreXf().setApplyFill(true);

            return style;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private XSSFCellStyle createNewCellStyle(XSSFWorkbook workbook){
        
        XSSFCellStyle styleClaro = workbook.createCellStyle();
        styleClaro.setBorderBottom(BorderStyle.THIN);
        styleClaro.setBorderTop(BorderStyle.THIN);
        styleClaro.setBorderLeft(BorderStyle.THIN);
        styleClaro.setBorderRight(BorderStyle.THIN);
        
        return styleClaro;
    }
    
    private XSSFColor lerpColor(double delta, XSSFColor i, XSSFColor f){
         // Garante que delta esteja entre 0 e 1
        delta = Math.max(0f, Math.min(1f, delta));

        byte[] startRGB = i.getRGB();
        byte[] endRGB = f.getRGB();

        if (startRGB == null || endRGB == null) {
            throw new IllegalArgumentException("Uma das cores não possui RGB definido.");
        }

        byte[] resultRGB = new byte[3];

        for (int c = 0; c < 3; c++) {
            int start = startRGB[c] & 0xFF; // converte byte para 0-255
            int end = endRGB[c] & 0xFF;

            int value = (int) (start + delta * (end - start));
            resultRGB[c] = (byte) value;
        }

        return new XSSFColor(resultRGB, null);
    }
}
