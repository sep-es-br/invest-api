package br.gov.es.invest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import br.gov.es.invest.dto.RegistroDadoDetalhado;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorAno;
import br.gov.es.invest.dto.RegistroDadoDetalhadoValoresPorFonte;
import br.gov.es.invest.model.FonteOrcamentaria;

@Service
public class RelatorioService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    @Autowired
    private FonteOrcamentariaService fonteOrcamentariaService;

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

    private List<RegistroDadoDetalhado> getRegistroDadoDetalhados(
        String tipoDespesa, List<String> idsUnidade, List<String> idsPlanos, String idFonte, Integer gnd, Integer anoInicio, Integer anoFim 
        ){
        

        String cypher = "WITH\r\n" + //
                        "    $tipoDespesa AS _tpDespesa,\r\n" + //
                        "    $unidades AS _unidadeOrcamentaria,\r\n" + //
                        "    $planos AS _planoOrcamentario,\r\n" + //
                        "    $fonte AS _idFonte,\r\n" + //
                        "    $gnd AS _gnd\r\n" + //
                        "\r\n" + //
                        "MATCH  \r\n" + //
                        "    (unidade:UnidadeOrcamentaria)-[:IMPLEMENTA]->(conta:Conta)<-[:ORIENTA]-(po:PlanoOrcamentario),\r\n" + //
                        "    (conta)<-[:CUSTEADO]-(obj:Objeto)\r\n" + //
                        "    \r\n" + //
                        "WHERE  \r\n" + //
                        "    _tpDespesa IN LABELS(conta)\r\n" + //
                        "    AND NOT EXISTS((obj)-[:EM]->(:Etapa))\r\n" + //
                        "    AND (_unidadeOrcamentaria IS NULL OR elementId(unidade) IN _unidadeOrcamentaria)\r\n" + //
                        "    AND (_planoOrcamentario IS NULL OR elementId(po) IN _planoOrcamentario)\r\n" + //
                        "\r\n" + //
                        "MATCH (obj)<-[:ESTIMADO]-(:Custo)-[indicada_por:INDICADA_POR]->(:FonteOrcamentaria)\r\n" + //
                        "\r\n" + //
                        "OPTIONAL MATCH (obj)-[:SOBRE]->(areaTematica:AreaTematica)\r\n" + //
                        "OPTIONAL MATCH (obj)-[:ATENDE]->(microrregiao:Localidade)\r\n" + //
                        "OPTIONAL MATCH (obj)-[:DO_TIPO]->(tipoPlano:TipoPlano)\r\n" + //
                        "OPTIONAL MATCH (obj)<-[:RESPONSAVEL_POR]-(usuario:Usuario)\r\n" + //
                        "\r\n" + //
                        "WITH \r\n" + //
                        "    unidade.codigo AS codUnidade,\r\n" + //
                        "    unidade.codigo + ' - ' + unidade.sigla AS unidadeResponsavel,\r\n" + //
                        "    COALESCE(usuario.nomeCompleto, \"-\") AS nomeResponsavel,\r\n" + //
                        "    po.codigo AS codPO,\r\n" + //
                        "    po.nome AS nomePO,\r\n" + //
                        "    obj.descricao AS descObjeto,\r\n" + //
                        "    CASE WHEN tipoPlano IS NULL THEN { sigla: 'PIP'} ELSE tipoPlano END AS tiposPo,\r\n" + //
                        "    microrregiao.nome AS microrregiao,\r\n" + //
                        "    areaTematica.nome AS areaTematica,\r\n" + //
                        "    CASE WHEN obj.contrato IS NULL OR obj.contrato = '' THEN '-' ELSE obj.contrato END AS contrato,\r\n" + //
                        "    COALESCE(indicada_por.gnd, -1) AS gnd,\r\n" + //
                        "    elementId(obj) AS objetoId\r\n" + //
                        "\r\n" + //
                        "RETURN DISTINCT\r\n" + //
                        "    codUnidade,\r\n" + //
                        "    unidadeResponsavel,\r\n" + //
                        "    nomeResponsavel,\r\n" + //
                        "    codPO,\r\n" + //
                        "    nomePO,\r\n" + //
                        "    descObjeto,\r\n" + //
                        "    apoc.text.join(collect(DISTINCT tiposPo.sigla), '; ') AS tiposPo,\r\n" + //
                        "    COALESCE(microrregiao, ' - ') AS microrregiao,\r\n" + //
                        "    COALESCE(areaTematica, ' - ') AS areaTematica,\r\n" + //
                        "    contrato,\r\n" + //
                        "    gnd,\r\n" + //
                        "    objetoId\r\n" + //
                        "ORDER BY codUnidade, codPO;\r\n" + //
                        "\r\n";

        String cypherAnos = "MATCH (custo:Custo)\r\n" + //
                            "WHERE custo.anoExercicio >= $anoInicio\r\n" + //
                            "  AND custo.anoExercicio <= $anoFim\r\n" + //
                            "RETURN DISTINCT custo.anoExercicio AS ano\r\n" + //
                            "ORDER BY ano";

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

        String cypherPrevistoContratado = "MATCH (objeto:Objeto)\r\n" + //
                                                "WHERE elementId(objeto) = $idObjeto\r\n" + //
                                                "OPTIONAL MATCH (objeto)<-[:ESTIMADO]-(custo:Custo)\r\n" + //
                                                "WHERE custo.anoExercicio = $ano\r\n" + //
                                                "OPTIONAL MATCH (custo)-[indicada_por:INDICADA_POR]->(fonteOrcamentaria:FonteOrcamentaria)\r\n" + //
                                                "WHERE elementId(fonteOrcamentaria) = $idFonte\r\n" + //
                                                "RETURN COALESCE(indicada_por.previsto, 0) AS previsto,\r\n" + //
                                                "        COALESCE(indicada_por.contratado, 0) AS contratado";

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

}
