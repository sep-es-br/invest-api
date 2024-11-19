package br.gov.es.invest.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class DataMock {
    
    private static final Logger LOGGER = Logger.getLogger(DataMock.class.getSimpleName()); 

    public static boolean populado = false;

    public static ArrayList<Custo> noCustos = new ArrayList<>();
    public static ArrayList<Objeto> noObjetos = new ArrayList<>();
    public static ArrayList<Investimento> noInvestimentos = new ArrayList<>();
    public static ArrayList<ExecucaoOrcamentaria> noExecucaoOrcamentarias = new ArrayList<>();
    public static ArrayList<PlanoOrcamentario> noPlanoOrcamentarios = new ArrayList<>();
    public static ArrayList<UnidadeOrcamentaria> noUnidadeOrcamentarias = new ArrayList<>();
    public static ArrayList<FonteOrcamentaria> noFonteOrcamentarias = new ArrayList<>();

    public static void popularMock(){

        Investimento invAquisicaoDeFrotas = new Investimento("Aquisição de Frotas");
        Investimento invEsDigital = new Investimento("ES DIGITAL");
        Investimento invMacrodrenagem = new Investimento("EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.");
        Investimento invCaisArtes = new Investimento("Cais das Artes");
        Investimento invSinalizacoes = new Investimento("SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES");
        Investimento invObras = new Investimento("Obras e Supervisão do Caminhos do Campo");
        Investimento invCentroExce = new Investimento("CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES");
        Investimento invModeResso = new Investimento("MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO");
        Investimento invGestaoBens = new Investimento("GESTÃO DE BENS PATRIMONIAIS");
        Investimento invIEMA = new Investimento("IEMA Digital");
        Investimento invGestaoEspaco = new Investimento("GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT");
        Investimento invProjetosEspaciais = new Investimento("PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO");
        Investimento invConstrCampo = new Investimento("CONSTRUÇÃO DE CAMPO BOM DE BOLA");
        Investimento invNovoFundo = new Investimento("NOVO FUNDO CIDADES");
        Investimento invCentroEvento = new Investimento("CENTRO DE EVENTOS DO MORANGÃO");
        Investimento invProjResso = new Investimento("PROJETOS DE RESSOCIALIZAÇÃO");
        Investimento invArcoViario = new Investimento("ARCO VIÁRIO DE COLATINA COM 4º PONTE");
        Investimento invCarroTransp = new Investimento("CARRO DE TRANSPORTE DE AERONAVES");

        noInvestimentos.addAll(Arrays.asList(
            invAquisicaoDeFrotas, invEsDigital, invMacrodrenagem, invCaisArtes,
            invSinalizacoes, invObras, invCentroExce, invModeResso, invGestaoBens,
            invIEMA, invGestaoEspaco, invProjetosEspaciais, invConstrCampo,
            invNovoFundo, invCentroEvento, invProjResso, invArcoViario,
            invCarroTransp
        ) );
        


        ExecucaoOrcamentaria execucaoAquisicao2025F1 = new ExecucaoOrcamentaria("2025", 4377049d, 0, null, invAquisicaoDeFrotas);
        ExecucaoOrcamentaria execucaoAquisicao2025F2 = new ExecucaoOrcamentaria("2025", 4377049d, 0, null, invAquisicaoDeFrotas);

        ExecucaoOrcamentaria execucaoEsDigital = new ExecucaoOrcamentaria("2025", 0, 0, null, invEsDigital);

        ExecucaoOrcamentaria execucaoMacrodrenagem = new ExecucaoOrcamentaria("2025", 0, 0, null, invMacrodrenagem);

        ExecucaoOrcamentaria execucaoCais2025F1 = new ExecucaoOrcamentaria("2025", 216000000d, 0, null, invCaisArtes);

        ExecucaoOrcamentaria execucaoSinalizacao2025F2 = new ExecucaoOrcamentaria("2025", 34800000d, 0, null, invSinalizacoes);

        ExecucaoOrcamentaria execucaoObras2025F1 = new ExecucaoOrcamentaria("2025", 372481632d, 0, null, invObras);

        ExecucaoOrcamentaria execucaoCentroExce = new ExecucaoOrcamentaria("2025", 0, 0, null, invCentroExce);

        ExecucaoOrcamentaria execucaoModeResso2025F1 = new ExecucaoOrcamentaria("2025", 2111050, 0, null, invModeResso);

        ExecucaoOrcamentaria execucaoGestaoBens12025F1 = new ExecucaoOrcamentaria("2025", 14663926, 0, null, invGestaoBens);
        ExecucaoOrcamentaria execucaoGestaoBens22025F1 = new ExecucaoOrcamentaria("2025", 645671, 0, null, invGestaoBens);

        ExecucaoOrcamentaria execucaoIEMA = new ExecucaoOrcamentaria("2025", 0, 0, null, invIEMA);

        ExecucaoOrcamentaria execucaoGestaoEspaco12025F1 = new ExecucaoOrcamentaria("2025", 3900000, 0, null, invGestaoEspaco);

        ExecucaoOrcamentaria execucaoProjetosEspaciais = new ExecucaoOrcamentaria("2025", 0, 0, null, invProjetosEspaciais);

        ExecucaoOrcamentaria execucaoConstrCampo2025F1 = new ExecucaoOrcamentaria("2025", 50000, 0, null, invConstrCampo);

        ExecucaoOrcamentaria execucaoNovoFundo2025F1 = new ExecucaoOrcamentaria("2025", 24750000, 0, null, invNovoFundo);

        ExecucaoOrcamentaria execucaoCentroEvento2025F1 = new ExecucaoOrcamentaria("2025", 708000, 0, null, invCentroEvento);

        ExecucaoOrcamentaria execucaoProjResso2025F2 = new ExecucaoOrcamentaria("2025", 390000, 0, null, invProjResso);

        ExecucaoOrcamentaria execucaoArcoViario2025F1 = new ExecucaoOrcamentaria("2025", 250000, 0, null, invArcoViario);

        ExecucaoOrcamentaria execucaoCarroTransp = new ExecucaoOrcamentaria("2025", 0, 0, null, invCarroTransp);

        noExecucaoOrcamentarias.addAll(Arrays.asList(
                execucaoAquisicao2025F1, execucaoAquisicao2025F2, execucaoCais2025F1,
                execucaoSinalizacao2025F2, execucaoObras2025F1, execucaoModeResso2025F1,
                execucaoGestaoBens12025F1, execucaoGestaoBens22025F1, execucaoGestaoEspaco12025F1,
                execucaoConstrCampo2025F1, execucaoNovoFundo2025F1, execucaoCentroEvento2025F1,
                execucaoProjResso2025F2, execucaoArcoViario2025F1, execucaoEsDigital, execucaoMacrodrenagem
            ));

        PlanoOrcamentario planoAquisicaoFrota = new PlanoOrcamentario(831l, "AQUISIÇÃO DE FROTAS",
        Arrays.asList(
            execucaoAquisicao2025F1, execucaoAquisicao2025F2
        ));

        PlanoOrcamentario planoEsDigital = new PlanoOrcamentario(1022l, "ES DIGITAL", Arrays.asList(execucaoEsDigital));

        PlanoOrcamentario planoDrenagem = new PlanoOrcamentario(1083l, "EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.",  Arrays.asList(execucaoMacrodrenagem));

        PlanoOrcamentario planoCaisArtes = new PlanoOrcamentario(1242l, "Cais das Artes", Arrays.asList(execucaoCais2025F1));

        PlanoOrcamentario planoSinalizacao = new PlanoOrcamentario(1213l, "SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES",
            Arrays.asList(execucaoSinalizacao2025F2));

        PlanoOrcamentario planoObras = new PlanoOrcamentario(1870l, "Obras e Supervisão do Caminhos do Campo",
            Arrays.asList(execucaoObras2025F1));

        PlanoOrcamentario planoCentroExce = new PlanoOrcamentario(2118l, "CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES", Arrays.asList(execucaoCentroExce));

        PlanoOrcamentario planoModeResso = new PlanoOrcamentario(2120l, "MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO",
            Arrays.asList(execucaoModeResso2025F1)
        );

        PlanoOrcamentario planoGestaoBens = new PlanoOrcamentario(2131l, "GESTÃO DE BENS PATRIMONIAIS", 
            Arrays.asList( execucaoGestaoBens12025F1, execucaoGestaoBens22025F1 )
        );

        PlanoOrcamentario planoIEMA = new PlanoOrcamentario(2156l, "IEMA Digital", Arrays.asList(execucaoIEMA));

        PlanoOrcamentario planoGestaoEspaco = new PlanoOrcamentario(2184l, "GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT",
            Arrays.asList(execucaoGestaoEspaco12025F1)
        );

        PlanoOrcamentario planoProjEspaciais = new PlanoOrcamentario(2278l, "PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO",
            Arrays.asList(execucaoProjetosEspaciais)
        );

        PlanoOrcamentario planoConstrCampo = new PlanoOrcamentario(2321l, "CONSTRUÇÃO DE CAMPO BOM DE BOLA",
            Arrays.asList(execucaoConstrCampo2025F1)
        );

        PlanoOrcamentario planoNovoFundo = new PlanoOrcamentario(2344l, "NOVO FUNDO CIDADES", 
            Arrays.asList(execucaoNovoFundo2025F1)
        );

        PlanoOrcamentario planoCentroEventos = new PlanoOrcamentario(2355l, "CENTRO DE EVENTOS DO MORANGÃO", 
            Arrays.asList(execucaoCentroEvento2025F1)
        );

        PlanoOrcamentario planoProjResso = new PlanoOrcamentario(2381l, "PROJETOS DE RESSOCIALIZAÇÃO",
            Arrays.asList(execucaoProjResso2025F2)
        );

        PlanoOrcamentario planoArcoViario = new PlanoOrcamentario(2414l, "ARCO VIÁRIO DE COLATINA COM 4º PONTE",
            Arrays.asList(execucaoArcoViario2025F1)
        );

        PlanoOrcamentario planoCarroTransp = new PlanoOrcamentario(2423l, "CARRO DE TRANSPORTE DE AERONAVES", 
            Arrays.asList(execucaoCarroTransp)
        );

        noPlanoOrcamentarios.addAll(Arrays.asList(
            planoAquisicaoFrota, planoEsDigital, planoDrenagem, planoCaisArtes,
            planoSinalizacao, planoObras, planoCentroExce, planoModeResso,
            planoGestaoBens, planoIEMA, planoGestaoEspaco, planoProjEspaciais,
            planoConstrCampo, planoNovoFundo, planoCentroEventos, planoProjResso,
            planoArcoViario, planoCarroTransp
        ));


        UnidadeOrcamentaria uniPCES = new UnidadeOrcamentaria(45102l, "PCES",
            Arrays.asList(
                planoAquisicaoFrota
            ), 
            Arrays.asList(
                execucaoAquisicao2025F1
            ));

        UnidadeOrcamentaria uniPMES = new UnidadeOrcamentaria(45103l, "PMES", 
            Arrays.asList(
                planoAquisicaoFrota
            ),
            Arrays.asList(
                execucaoAquisicao2025F1
            ));
        UnidadeOrcamentaria uniPCIES = new UnidadeOrcamentaria(45107l, "PCIES", 
            Arrays.asList(
                planoAquisicaoFrota
            ),
            Arrays.asList(
                execucaoAquisicao2025F1
            ));
        UnidadeOrcamentaria uniFUNREBOM = new UnidadeOrcamentaria(45904l, "FUNREBOM", 
            Arrays.asList(
                planoAquisicaoFrota
            ),
            Arrays.asList(execucaoAquisicao2025F2));
        UnidadeOrcamentaria uniSEDH = new UnidadeOrcamentaria(48101l, "SEDH", 
            Arrays.asList(
                planoAquisicaoFrota, planoModeResso
            ), 
            Arrays.asList(
                execucaoAquisicao2025F1, execucaoModeResso2025F1
            ));
        UnidadeOrcamentaria uniPRODEST = new UnidadeOrcamentaria(28203l, "PRODEST", 
            Arrays.asList( planoEsDigital ), 
            Arrays.asList( execucaoEsDigital )
        );
        UnidadeOrcamentaria uniSEDU = new UnidadeOrcamentaria(42101l, "SEDU", 
            Arrays.asList( planoEsDigital ), 
            Arrays.asList( execucaoEsDigital )
        );
        UnidadeOrcamentaria uniSEDURB = new UnidadeOrcamentaria(36101l, "SEDURB", 
            Arrays.asList( planoDrenagem ), 
            Arrays.asList( execucaoMacrodrenagem )
        );

        UnidadeOrcamentaria uniSECULT = new UnidadeOrcamentaria(40101l, "SECULT",
            Arrays.asList( planoCaisArtes, planoGestaoEspaco ), 
            Arrays.asList( execucaoCais2025F1, execucaoGestaoEspaco12025F1 ));

        UnidadeOrcamentaria uniDERES = new UnidadeOrcamentaria(35201l, "DER-ES", 
            Arrays.asList(planoSinalizacao, planoArcoViario), 
            Arrays.asList(execucaoSinalizacao2025F2, execucaoArcoViario2025F1));

        UnidadeOrcamentaria uniSEAG = new UnidadeOrcamentaria(31101l, "SEAG",
            Arrays.asList(planoObras, planoCentroEventos),
            Arrays.asList(execucaoObras2025F1,  execucaoCentroEvento2025F1));

        UnidadeOrcamentaria uniFEAC = new UnidadeOrcamentaria(31901l, "FEAC",
            Arrays.asList(planoObras),
            Arrays.asList(execucaoObras2025F1));

        UnidadeOrcamentaria uniSESPORT = new UnidadeOrcamentaria(39101l, "SESPORT",
            Arrays.asList(planoCentroExce, planoConstrCampo),
            Arrays.asList(execucaoConstrCampo2025F1, execucaoCentroExce));

        UnidadeOrcamentaria uniPROESPORTE = new UnidadeOrcamentaria(39901l, "PRÓ-ESPORTE",
            Arrays.asList(planoCentroExce),
            Arrays.asList(execucaoCentroExce));

        UnidadeOrcamentaria uniSEJUS = new UnidadeOrcamentaria(46101l, "SEJUS",
            Arrays.asList(planoGestaoBens, planoProjEspaciais, planoProjResso),
            Arrays.asList(execucaoGestaoBens12025F1, execucaoProjetosEspaciais));
        
        UnidadeOrcamentaria uniPPES = new UnidadeOrcamentaria(46113l, "PPES",
            Arrays.asList(planoGestaoBens),
            Arrays.asList(execucaoGestaoBens22025F1));
        
        UnidadeOrcamentaria uniIEMA = new UnidadeOrcamentaria(41201l, "IEMA",
            Arrays.asList(planoIEMA),
            Arrays.asList(execucaoIEMA));

        UnidadeOrcamentaria uniFUNDEMA = new UnidadeOrcamentaria(41901l, "FUNDEMA",
            Arrays.asList(planoIEMA),
            Arrays.asList(execucaoIEMA));

        UnidadeOrcamentaria uniFEADM = new UnidadeOrcamentaria(10906l, "FEADM", 
            Arrays.asList(planoNovoFundo), 
            Arrays.asList(execucaoNovoFundo2025F1));

        UnidadeOrcamentaria uniFTP = new UnidadeOrcamentaria(46901l, "FTP",
            Arrays.asList(planoProjResso),
            Arrays.asList(execucaoProjResso2025F2));

        UnidadeOrcamentaria uniFUNPEN = new UnidadeOrcamentaria(46903l, "FUNPEN",
            Arrays.asList(planoProjResso),
            Arrays.asList(execucaoProjResso2025F2));

        UnidadeOrcamentaria uniSCM = new UnidadeOrcamentaria(10102l, "SCM",
            Arrays.asList(planoCarroTransp),
            Arrays.asList(execucaoCarroTransp));

        noUnidadeOrcamentarias.addAll(Arrays.asList(
            uniPCES, uniPMES, uniPCIES, uniFUNREBOM, uniSEDH, uniPRODEST, uniSEDU,
            uniSEDURB, uniSECULT, uniDERES, uniSEAG, uniFEAC, uniSESPORT, uniPROESPORTE,
            uniSEJUS, uniPPES, uniIEMA, uniFUNDEMA, uniFEADM, uniFTP, uniFUNPEN, uniSCM
        ));



        Objeto objAquiFrot1 = new Objeto("AQUISIÇÃO DE FROTAS", "Entrega", invAquisicaoDeFrotas);
        Objeto objAquiFrot2 = new Objeto("Aquisição de frota", "Entrega", invAquisicaoDeFrotas);
        Objeto objAquiFrot3 = new Objeto("AQUISIÇÃO DE FROTAS", "Entrega", invAquisicaoDeFrotas);
        Objeto objAquiFrot4 = new Objeto("AQUISIÇÃO DE FROTA", "Entrega", invAquisicaoDeFrotas);
        Objeto objAquiFrot5 = new Objeto("AQUISIÇÃO DE VEÍCULOS SUV - IASES", "Entrega", invAquisicaoDeFrotas);

        Objeto objDigital1 = new Objeto("ES DIGITAL", "Projeto", invEsDigital);
        Objeto objDigital2 = new Objeto("ES DIGITAL", "Projeto", invEsDigital);

        Objeto objDrenagem = new Objeto("EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.", "Entrega", invMacrodrenagem);

        Objeto objCaisArtes = new Objeto("Cais das Artes", "Projeto", invCaisArtes);

        Objeto objImplMan = new Objeto(
            "IMPLANTAÇÃO, MANUTENÇÃO E GERENCIAMENTO DOS SISTEMAS DE SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES",
            "Entrega", invSinalizacoes);

        Objeto objObras1 = new Objeto("Obras e Supervisão do Caminhos do Campo", "Entrega", invObras);
        Objeto objObras2 = new Objeto("Obras e Supervisão do Caminhos do Campo", "Entrega", invObras);

        Objeto objCentroExce1 = new Objeto(
            "CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES PARA PESSOAS COM DEFICIÊNCIAS",
            "Entrega", invCentroExce);
        Objeto objCentroExce2 = new Objeto(
            "CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES PARA PESSOAS COM DEFICIÊNCIAS",
            "Entrega", invCentroExce);

        Objeto objModeResso = new Objeto("MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO", "Projeto", invModeResso);

        Objeto objGestaoBens1 = new Objeto("GESTÃO DE BENS PATRIMONIAIS", "Projeto", invGestaoBens);
        Objeto objGestaoBens2 = new Objeto("GESTÃO DE BENS PATRIMONIAIS", "Projeto", invGestaoBens);

        Objeto objIEMA1 = new Objeto("IEMA DIGITAL", "Projeto", invIEMA);
        Objeto objIEMA2 = new Objeto("IMPLANTAÇÃO E ESTRUTURAÇÃO DE SERVIÇOS DIGITAIS", "Projeto", invIEMA);

        Objeto objGestaoEspaco = new Objeto("GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT", "Projeto", invGestaoEspaco);

        Objeto objProjEspaciais = new Objeto("PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO", "Projeto", invProjetosEspaciais);

        Objeto objConstrCampo = new Objeto("CONSTRUÇÃO DE CAMPO BOM DE BOLA", "Projeto", invConstrCampo);

        Objeto objNovoFundo = new Objeto("NOVO FUNDO CIDADES", "Projeto", invNovoFundo);

        Objeto objCentroEventos = new Objeto("CENTRO DE EVENTOS DO MORANGÃO - DOMINGOS MARTINS", "Entrega", invCentroEvento);

        Objeto objProjResso1 = new Objeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", invProjResso);
        Objeto objProjResso2 = new Objeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", invProjResso);
        Objeto objProjResso3 = new Objeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", invProjResso);

        Objeto objArcoViario = new Objeto("ARCO VIÁRIO DE COLATINA COM 4º PONTE", "Entrega", invArcoViario);

        Objeto objCarroTransp = new Objeto("CARRO DE TRANSPORTE DE AERONAVES", "Entrega", invCarroTransp);

        noObjetos.addAll(Arrays.asList(
            objAquiFrot1, objAquiFrot2, objAquiFrot3, objAquiFrot4, objAquiFrot5,
            objDigital1, objDigital2, objDrenagem, objCaisArtes, objImplMan,
            objObras1, objObras2, objCentroExce1, objCentroExce2, objModeResso,
            objGestaoBens1, objGestaoBens2, objIEMA1, objIEMA2, objGestaoEspaco,
            objProjEspaciais, objConstrCampo, objNovoFundo, objCentroEventos,
            objProjResso1 , objProjResso2, objProjResso3, objArcoViario,
            objCarroTransp
        ));
        
        Custo custoObj12025 = new Custo("2025", 26000000d, 26000000, objAquiFrot1);
        Custo custoObj22025 = new Custo("2025", 82250000d, 0, objAquiFrot2);
        Custo custoObj32025 = new Custo("2025", 6000000d, 0, objAquiFrot3);
        Custo custoObj42025 = new Custo("2024", 19023774.48d, 0, objAquiFrot4);
        
        Custo custoObj12026 = new Custo("2026",  26000000.00d, 0, objAquiFrot1);
        Custo custoObj22026 = new Custo("2026", 82250000d, 0, objAquiFrot2);
        Custo custoObj32026 = new Custo("2026", 6000000d, 0, objAquiFrot3);
        Custo custoObj42026 = new Custo("2026", 19023774.48d, 0, objAquiFrot4);
        
        Custo custoObj12027 = new Custo("2027",  26000000.00d, 0, objAquiFrot1);
        Custo custoObj22027 = new Custo("2027", 72000000d, 0, objAquiFrot2);
        Custo custoObj32027 = new Custo("2027", 6000000d, 0, objAquiFrot3);
        Custo custoObj42027 = new Custo("2027", 13587775.48d, 0, objAquiFrot4);

        Custo custoObjDrenagem2025 = new Custo("2025", 109915909.28d, 0, objDrenagem);

        Custo custoObjCais2025 = new Custo("2025", 74633732.98, 0, objCaisArtes);

        Custo custoObjImpl2025 = new Custo("2025", 50000000, 50000000, objImplMan);
        Custo custoObjImpl2026 = new Custo("2026", 50000000, 50000000, objImplMan);
        Custo custoObjImpl2027 = new Custo("2027", 50000000, 50000000, objImplMan);

        Custo custoObjObras12025 = new Custo("2025", 8994010.17, 0, objObras1);
        Custo custoObjObras22026 = new Custo("2026", 3800000, 0, objObras1);
        Custo custoObjObras32027 = new Custo("2027", 3800000, 0, objObras1);

        Custo custoCentroExce12025F1 = new Custo("2025", 2476310.12, 0, objCentroExce1);
        Custo custoCentroExce22025F2 = new Custo("2025", 1159032.68, 1159032.68, objCentroExce2);

        Custo custoGestaoBens12025F1 = new Custo("2025", 2500000, 0, objGestaoBens1);
        Custo custoGestaoBens12026F1 = new Custo("2026", 2500000, 0, objGestaoBens1);
        Custo custoGestaoBens12027F1 = new Custo("2027", 2500000, 0, objGestaoBens1);
        
        Custo custoGestaoBens22025F1 = new Custo("2025", 1895541.16, 0, objGestaoBens2);
        Custo custoGestaoBens22026F1 = new Custo("2026", 1263694.11, 0, objGestaoBens2);
        Custo custoGestaoBens22027F1 = new Custo("2027", 1263694.11, 0, objGestaoBens2);

        Custo custoGestaoEspaco2025F1 = new Custo("2025", 2000000, 0, objGestaoEspaco);
        Custo custoGestaoEspaco2026F1 = new Custo("2025", 2000000, 0, objGestaoEspaco);

        Custo custoProjEspaciais2025F1 = new Custo("2025", 2344000, 0, objProjEspaciais);
        Custo custoProjEspaciais2026F1 = new Custo("2026", 2344000, 0, objProjEspaciais);
        Custo custoProjEspaciais2027F1 = new Custo("2027", 2344000, 0, objProjEspaciais);

        Custo custoConstrCampo2025F1 = new Custo("2025", 12673860.73, 30000000, objConstrCampo);
        Custo custoConstrCampo2025F2 = new Custo("2025",  500000.00, 500000, objConstrCampo);
        Custo custoConstrCampo2026F1 = new Custo("2026",  0, 15000000, objConstrCampo);
        Custo custoConstrCampo2027F1 = new Custo("2027",  0, 25000000, objConstrCampo);

        Custo custoNovoFundo2025F1 = new Custo("2025", 12526388.07, 12526388.07, objNovoFundo);
        Custo custoNovoFundo2026F1 = new Custo("2026", 240000000, 0, objNovoFundo);
        Custo custoNovoFundo2027F1 = new Custo("2027", 240000000, 0, objNovoFundo);

        Custo custoArcoViario2025F1 = new Custo("2025", 1243665.16, 1243665.16, objArcoViario);

        Custo custoCarroTransp2025F1 = new Custo("2025", 250000, 0, objCarroTransp);

        noCustos.addAll(Arrays.asList(
            custoObj12025, custoObj22025, custoObj32025, custoObj42025,
            custoObj12026, custoObj22026, custoObj32026, custoObj42026,
            custoObj12027, custoObj22027, custoObj32027, custoObj42027,
            custoObjDrenagem2025, custoObjCais2025,
            custoObjImpl2025, custoObjImpl2026, custoObjImpl2027,
            custoObjObras12025, custoObjObras22026, custoObjObras32027,
            custoCentroExce12025F1, custoCentroExce22025F2,
            custoGestaoBens12025F1, custoGestaoBens12026F1, custoGestaoBens12027F1,
            custoGestaoBens22025F1, custoGestaoBens22026F1, custoGestaoBens22027F1,
            custoGestaoEspaco2025F1, custoGestaoEspaco2026F1, custoProjEspaciais2025F1,
            custoProjEspaciais2026F1, custoProjEspaciais2027F1, custoConstrCampo2025F1,
            custoConstrCampo2025F2, custoConstrCampo2026F1, custoConstrCampo2027F1,
            custoNovoFundo2025F1, custoNovoFundo2026F1, custoNovoFundo2027F1,
            custoArcoViario2025F1, custoCarroTransp2025F1
        ));

        FonteOrcamentaria fonte1 = new FonteOrcamentaria("0","Fonte 1",
            Arrays.asList(
                custoObj12025, custoObj22025, custoObj32025, custoObj42025,
                custoObj12026, custoObj22026, custoObj32026, custoObj42026,
                custoObj12027, custoObj22027, custoObj32027, custoObj42027,
                custoObjDrenagem2025, custoObjCais2025,
                custoObjObras12025, custoObjObras22026, custoObjObras32027,
                custoCentroExce12025F1, custoGestaoBens12025F1, custoGestaoBens12026F1,
                custoGestaoBens12027F1, custoGestaoBens22025F1, custoGestaoBens22026F1,
                custoGestaoBens22027F1, custoGestaoEspaco2025F1, custoGestaoEspaco2026F1,
                custoProjEspaciais2025F1, custoProjEspaciais2026F1, custoProjEspaciais2027F1,
                custoConstrCampo2025F1, custoConstrCampo2026F1, custoConstrCampo2027F1,
                custoNovoFundo2025F1, custoNovoFundo2026F1, custoNovoFundo2027F1,
                custoArcoViario2025F1, custoCarroTransp2025F1
            ),
            Arrays.asList(
                execucaoAquisicao2025F1, execucaoCais2025F1, execucaoObras2025F1,
                execucaoModeResso2025F1, execucaoGestaoBens12025F1, execucaoGestaoBens22025F1,
                execucaoGestaoEspaco12025F1, execucaoConstrCampo2025F1, execucaoNovoFundo2025F1,
                execucaoCentroEvento2025F1, execucaoArcoViario2025F1, execucaoEsDigital
            )
        );
        FonteOrcamentaria fonte2 = new FonteOrcamentaria("1", "Fonte 2",
            Arrays.asList( 
                custoObjImpl2025, custoObjImpl2026, custoObjImpl2027, custoCentroExce22025F2,
                custoConstrCampo2025F2
            ),
            Arrays.asList( execucaoAquisicao2025F2, execucaoSinalizacao2025F2, execucaoProjResso2025F2 ));
        
        noFonteOrcamentarias.addAll(Arrays.asList(fonte1, fonte2));



        populado = true;

    }



    public static void popular2(){

        // Investimentos
        Investimento invAquisicaoDeFrotas = Investimento.criar("Aquisição de Frotas");
        Investimento invEsDigital = Investimento.criar("ES DIGITAL");
        Investimento invMacrodrenagem = Investimento.criar("EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.");
        Investimento invCaisArtes = Investimento.criar("Cais das Artes");
        Investimento invSinalizacoes = Investimento.criar("SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES");
        Investimento invObras = Investimento.criar("Obras e Supervisão do Caminhos do Campo");
        Investimento invCentroExce = Investimento.criar("CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES");
        Investimento invModeResso = Investimento.criar("MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO");
        Investimento invGestaoBens = Investimento.criar("GESTÃO DE BENS PATRIMONIAIS");
        Investimento invIEMA = Investimento.criar("IEMA Digital");
        Investimento invGestaoEspaco = Investimento.criar("GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT");
        Investimento invProjetosEspaciais = Investimento.criar("PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO");
        Investimento invConstrCampo = Investimento.criar("CONSTRUÇÃO DE CAMPO BOM DE BOLA");
        Investimento invNovoFundo = Investimento.criar("NOVO FUNDO CIDADES");
        Investimento invCentroEvento = Investimento.criar("CENTRO DE EVENTOS DO MORANGÃO");
        Investimento invProjResso = Investimento.criar("PROJETOS DE RESSOCIALIZAÇÃO");
        Investimento invArcoViario = Investimento.criar("ARCO VIÁRIO DE COLATINA COM 4º PONTE");
        Investimento invCarroTransp = Investimento.criar("CARRO DE TRANSPORTE DE AERONAVES");

        // Unidades Orçamentarias
        UnidadeOrcamentaria uniPCES = UnidadeOrcamentaria.criar(45102l, "PCES");
        UnidadeOrcamentaria uniPMES = UnidadeOrcamentaria.criar(45103l, "PMES");
        UnidadeOrcamentaria uniPCIES = UnidadeOrcamentaria.criar(45107l, "PCIES");
        UnidadeOrcamentaria uniFUNREBOM = UnidadeOrcamentaria.criar(45904l, "FUNREBOM");
        UnidadeOrcamentaria uniSEDH = UnidadeOrcamentaria.criar(48101l, "SEDH");
        UnidadeOrcamentaria uniPRODEST = UnidadeOrcamentaria.criar(28203l, "PRODEST");
        UnidadeOrcamentaria uniSEDU = UnidadeOrcamentaria.criar(42101l, "SEDU");
        UnidadeOrcamentaria uniSEDURB = UnidadeOrcamentaria.criar(36101l, "SEDURB");
        UnidadeOrcamentaria uniSECULT = UnidadeOrcamentaria.criar(40101l, "SECULT");
        UnidadeOrcamentaria uniDERES = UnidadeOrcamentaria.criar(35201l, "DER-ES");
        UnidadeOrcamentaria uniSEAG = UnidadeOrcamentaria.criar(31101l, "SEAG");
        UnidadeOrcamentaria uniFEAC = UnidadeOrcamentaria.criar(31901l, "FEAC");
        UnidadeOrcamentaria uniSESPORT = UnidadeOrcamentaria.criar(39101l, "SESPORT");
        UnidadeOrcamentaria uniPROESPORTE = UnidadeOrcamentaria.criar(39901l, "PRÓ-ESPORTE");
        UnidadeOrcamentaria uniSEJUS = UnidadeOrcamentaria.criar(46101l, "SEJUS");
        UnidadeOrcamentaria uniPPES = UnidadeOrcamentaria.criar(46113l, "PPES");
        UnidadeOrcamentaria uniIEMA = UnidadeOrcamentaria.criar(41201l, "IEMA");
        UnidadeOrcamentaria uniFUNDEMA = UnidadeOrcamentaria.criar(41901l, "FUNDEMA");
        UnidadeOrcamentaria uniFEADM = UnidadeOrcamentaria.criar(10906l, "FEADM");
        UnidadeOrcamentaria uniFTP = UnidadeOrcamentaria.criar(46901l, "FTP");
        UnidadeOrcamentaria uniFUNPEN = UnidadeOrcamentaria.criar(46903l, "FUNPEN");
        UnidadeOrcamentaria uniSCM = UnidadeOrcamentaria.criar(10102l, "SCM");

        // Planos Orcamentarios
        PlanoOrcamentario planoAquisicaoFrota = PlanoOrcamentario.criar(831l, "AQUISIÇÃO DE FROTAS");
        PlanoOrcamentario planoEsDigital = PlanoOrcamentario.criar(1022l, "ES DIGITAL");
        PlanoOrcamentario planoDrenagem = PlanoOrcamentario.criar(1083l, "EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.");
        PlanoOrcamentario planoCaisArtes = PlanoOrcamentario.criar(1242l, "Cais das Artes");
        PlanoOrcamentario planoSinalizacao = PlanoOrcamentario.criar(1213l, "SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES");
        PlanoOrcamentario planoObras = PlanoOrcamentario.criar(1870l, "Obras e Supervisão do Caminhos do Campo");
        PlanoOrcamentario planoCentroExce = PlanoOrcamentario.criar(2118l, "CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES");
        PlanoOrcamentario planoModeResso = PlanoOrcamentario.criar(2120l, "MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO");
        PlanoOrcamentario planoGestaoBens = PlanoOrcamentario.criar(2131l, "GESTÃO DE BENS PATRIMONIAIS");
        PlanoOrcamentario planoIEMA = PlanoOrcamentario.criar(2156l, "IEMA Digital");
        PlanoOrcamentario planoGestaoEspaco = PlanoOrcamentario.criar(2184l, "GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT");
        PlanoOrcamentario planoProjEspaciais = PlanoOrcamentario.criar(2278l, "PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO");
        PlanoOrcamentario planoConstrCampo = PlanoOrcamentario.criar(2321l, "CONSTRUÇÃO DE CAMPO BOM DE BOLA");
        PlanoOrcamentario planoNovoFundo = PlanoOrcamentario.criar(2344l, "NOVO FUNDO CIDADES");
        PlanoOrcamentario planoCentroEventos = PlanoOrcamentario.criar(2355l, "CENTRO DE EVENTOS DO MORANGÃO");
        PlanoOrcamentario planoProjResso = PlanoOrcamentario.criar(2381l, "PROJETOS DE RESSOCIALIZAÇÃO");
        PlanoOrcamentario planoArcoViario = PlanoOrcamentario.criar(2414l, "ARCO VIÁRIO DE COLATINA COM 4º PONTE");
        PlanoOrcamentario planoCarroTransp = PlanoOrcamentario.criar(2423l, "CARRO DE TRANSPORTE DE AERONAVES");

        //Relação PO -> UO
        planoAquisicaoFrota.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniPCES, uniPMES, uniPCIES, uniFUNREBOM, uniSEDH
        ));

        planoEsDigital.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniPRODEST, uniSEDU
        ));

        planoDrenagem.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEDURB
        ));

        planoCaisArtes.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSECULT
        ));

        planoSinalizacao.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniDERES
        ));

        planoObras.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEAG, uniFEAC
        ));

        planoCentroExce.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSESPORT, uniPROESPORTE
        ));

        planoModeResso.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEDH
        ));

        planoGestaoBens.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEJUS, uniPPES
        ));

        planoIEMA.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniIEMA, uniFUNDEMA
        ));

        planoGestaoEspaco.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSECULT
        ));

        planoProjEspaciais.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEJUS
        ));

        planoConstrCampo.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSESPORT
        ));

        planoNovoFundo.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniFEADM
        ));

        planoCentroEventos.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEAG
        ));

        planoProjResso.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSEJUS, uniFTP, uniFUNPEN
        ));

        planoArcoViario.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniDERES
        ));

        planoCarroTransp.getUnidadesOrcamentariaControladoras().addAll(Arrays.asList(
            uniSCM
        ));

        // execuções Conta -> PO

        List<ExecucaoOrcamentaria> execucoesAquisicao = ExecucaoOrcamentaria.gerarExecucoes(planoAquisicaoFrota, invAquisicaoDeFrotas);
        List<ExecucaoOrcamentaria> execucoesEsDigital = ExecucaoOrcamentaria.gerarExecucoes(planoEsDigital, invEsDigital);
        List<ExecucaoOrcamentaria> execucoesMacrodrenagem = ExecucaoOrcamentaria.gerarExecucoes(planoDrenagem, invMacrodrenagem);
        List<ExecucaoOrcamentaria> execucoesCaisArte = ExecucaoOrcamentaria.gerarExecucoes(planoCaisArtes, invCaisArtes);
        List<ExecucaoOrcamentaria> execucoesSinalizacao = ExecucaoOrcamentaria.gerarExecucoes(planoSinalizacao, invSinalizacoes);
        List<ExecucaoOrcamentaria> execucoesObras = ExecucaoOrcamentaria.gerarExecucoes(planoObras, invObras);
        List<ExecucaoOrcamentaria> execucoesConstCentroExce = ExecucaoOrcamentaria.gerarExecucoes(planoCentroExce, invCentroExce);
        List<ExecucaoOrcamentaria> execucoesModerResso = ExecucaoOrcamentaria.gerarExecucoes(planoModeResso, invModeResso);
        List<ExecucaoOrcamentaria> execucoesGestaoBens = ExecucaoOrcamentaria.gerarExecucoes(planoGestaoBens, invGestaoBens);
        List<ExecucaoOrcamentaria> execucoesGestaoEspaco = ExecucaoOrcamentaria.gerarExecucoes(planoGestaoEspaco, invGestaoEspaco);
        List<ExecucaoOrcamentaria> execucoesIEMA = ExecucaoOrcamentaria.gerarExecucoes(planoIEMA, invIEMA);
        List<ExecucaoOrcamentaria> execucoesConstrCampo = ExecucaoOrcamentaria.gerarExecucoes(planoConstrCampo, invConstrCampo);
        List<ExecucaoOrcamentaria> execucoesProjEspaciais = ExecucaoOrcamentaria.gerarExecucoes(planoProjEspaciais, invProjetosEspaciais);
        List<ExecucaoOrcamentaria> execucoesNovoFundo = ExecucaoOrcamentaria.gerarExecucoes(planoNovoFundo, invNovoFundo);
        List<ExecucaoOrcamentaria> execucoesCentroEventos = ExecucaoOrcamentaria.gerarExecucoes(planoCentroEventos, invCentroEvento);
        List<ExecucaoOrcamentaria> execucoesProjResso = ExecucaoOrcamentaria.gerarExecucoes(planoProjResso, invProjResso);
        List<ExecucaoOrcamentaria> execucoesArcoViario = ExecucaoOrcamentaria.gerarExecucoes(planoArcoViario, invArcoViario);
        List<ExecucaoOrcamentaria> execucoesCarroTransp = ExecucaoOrcamentaria.gerarExecucoes(planoCarroTransp, invCarroTransp);

        // incluir as execuções nas contas/investimento

        invAquisicaoDeFrotas.setExecucoesOrcamentariaDelimitadores(execucoesAquisicao);
        invEsDigital.setExecucoesOrcamentariaDelimitadores(execucoesEsDigital);
        invMacrodrenagem.setExecucoesOrcamentariaDelimitadores(execucoesMacrodrenagem);

        invCaisArtes.setExecucoesOrcamentariaDelimitadores(execucoesCaisArte);

        invSinalizacoes.setExecucoesOrcamentariaDelimitadores(execucoesSinalizacao);

        invObras.setExecucoesOrcamentariaDelimitadores(execucoesObras);

        invCentroExce.setExecucoesOrcamentariaDelimitadores(execucoesConstCentroExce);

        invModeResso.setExecucoesOrcamentariaDelimitadores(execucoesModerResso);

        invGestaoBens.setExecucoesOrcamentariaDelimitadores(execucoesGestaoBens);

        invIEMA.setExecucoesOrcamentariaDelimitadores(execucoesIEMA);

        invGestaoEspaco.setExecucoesOrcamentariaDelimitadores(execucoesGestaoEspaco);

        invProjetosEspaciais.setExecucoesOrcamentariaDelimitadores(execucoesProjEspaciais);

        invConstrCampo.setExecucoesOrcamentariaDelimitadores(execucoesConstrCampo);

        invNovoFundo.setExecucoesOrcamentariaDelimitadores(execucoesNovoFundo);

        invCentroEvento.setExecucoesOrcamentariaDelimitadores(execucoesCentroEventos);

        invProjResso.setExecucoesOrcamentariaDelimitadores(execucoesProjResso);

        invArcoViario.setExecucoesOrcamentariaDelimitadores(execucoesArcoViario);
        
        invCarroTransp.setExecucoesOrcamentariaDelimitadores(execucoesCarroTransp);

        // gerar custo e objeto a partir da conta e unidade
        Custo custoAquisicaoPCES = gerarCustoComObjeto("AQUISIÇÃO DE FROTAS", "Entrega", uniPCES, invAquisicaoDeFrotas);
        Custo custoAquisicaoPMES = gerarCustoComObjeto("Aquisição de frota", "Entrega", uniPMES, invAquisicaoDeFrotas);
        Custo custoAquisicaoPCIES = gerarCustoComObjeto("AQUISIÇÃO DE FROTAS", "Entrega", uniPCIES, invAquisicaoDeFrotas);
        Custo custoAquisicaoFUNREBOM = gerarCustoComObjeto("AQUISIÇÃO DE FROTA", "Entrega", uniFUNREBOM, invAquisicaoDeFrotas);
        Custo custoAquisicaoSEDH = gerarCustoComObjeto("AQUISIÇÃO DE VEÍCULOS SUV - IASES", "Entrega", uniSEDH, invAquisicaoDeFrotas);
        Custo custoESDigitalPRODEST = gerarCustoComObjeto("ES DIGITAL", "Projeto", uniPRODEST, invEsDigital);
        Custo custoESDigitalSEDU = gerarCustoComObjeto("ES DIGITAL", "Projeto", uniSEDU, invEsDigital);
        Custo custoMacrodrenagemSEDURB = gerarCustoComObjeto("EXECUÇÃO DAS OBRAS DE MACRODRENAGEM DA BACIA DO CÓRREGO RIBEIRA.", "Entrega", uniSEDURB, invMacrodrenagem);
        Custo custoCaisSECULT = gerarCustoComObjeto("Cais das Artes", "Projeto", uniSECULT, invCaisArtes);
        Custo custoSinalizacaoDERES = gerarCustoComObjeto("IMPLANTAÇÃO, MANUTENÇÃO E GERENCIAMENTO DOS SISTEMAS DE SINALIZAÇÕES DAS RODOVIAS ESTADUAIS - DER-ES", "Entrega", uniDERES, invSinalizacoes);
        Custo custoObrasSEAG = gerarCustoComObjeto("Obras e Supervisão do Caminhos do Campo", "Entrega", uniSEAG, invObras);
        Custo custoObrasFEAC = gerarCustoComObjeto("Obras e Supervisão do Caminhos do Campo", "Entrega", uniFEAC, invObras);
        Custo custoCentroExceSESPORT = gerarCustoComObjeto("CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES PARA PESSOAS COM DEFICIÊNCIAS", "Entrega", uniSESPORT, invCentroExce);
        Custo custoCentroExcePROESPORTE = gerarCustoComObjeto("CONSTRUÇÃO DO CENTRO DE EXCELÊNCIA DE ESPORTES PARA PESSOAS COM DEFICIÊNCIAS", "Entrega", uniPROESPORTE, invCentroExce);
        Custo custoModerRessoSEDH = gerarCustoComObjeto("MODERNIZAÇÃO DO PROCESSO DE RESSOCIALIZAÇÃO", "Projeto", uniSEDH, invModeResso);
        Custo custoGestaoBensSEJUS = gerarCustoComObjeto("GESTÃO DE BENS PATRIMONIAIS", "Projeto", uniSEJUS, invGestaoBens);
        Custo custoGestaoBensPPES = gerarCustoComObjeto("GESTÃO DE BENS PATRIMONIAIS", "Projeto", uniPPES, invGestaoBens);
        Custo custoIemaIEMA = gerarCustoComObjeto("IEMA DIGITAL", "Projeto", uniIEMA, invIEMA);
        Custo custoIemaFUNDEMA = gerarCustoComObjeto("IMPLANTAÇÃO E ESTRUTURAÇÃO DE SERVIÇOS DIGITAIS", "Projeto", uniFUNDEMA, invIEMA);
        Custo custoGestaoEspacoSECULT = gerarCustoComObjeto("GESTÃO ESPAÇOS CULTURAIS E SEDE SECULT", "Projeto", uniSECULT, invGestaoEspaco);
        Custo custoProjEspeciaisSECULT = gerarCustoComObjeto("PROJETOS ESPECIAIS DO CONTRATO DE GESTÃO", "Projeto", uniSECULT, invProjetosEspaciais);
        Custo custoConstCampoSESPORT = gerarCustoComObjeto("CONSTRUÇÃO DE CAMPO BOM DE BOLA", "Projeto", uniSESPORT, invConstrCampo);
        Custo custoNovoFundoFEADM = gerarCustoComObjeto("NOVO FUNDO CIDADES", "Projeto", uniFEADM, invNovoFundo);
        Custo custoCentroEventosSEAG = gerarCustoComObjeto("CENTRO DE EVENTOS DO MORANGÃO - DOMINGOS MARTINS", "Entrega", uniSEAG, invCentroEvento);
        Custo custoProjetoRessoSEJUS = gerarCustoComObjeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", uniSEJUS, invProjResso);
        Custo custoProjetoRessoFTP = gerarCustoComObjeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", uniFTP, invProjResso);
        Custo custoProjetoRessoFUNPEN = gerarCustoComObjeto("PROJETOS DE RESSOCIALIZAÇÃO", "Projeto", uniFUNPEN, invProjResso);
        Custo custoArcoViarioDERES = gerarCustoComObjeto("ARCO VIÁRIO DE COLATINA COM 4º PONTE", "Entrega", uniDERES, invArcoViario);
        Custo custoCarroTransSCM = gerarCustoComObjeto("CARRO DE TRANSPORTE DE AERONAVES", "Entrega", uniSCM, invCarroTransp);

        FonteOrcamentaria fonte1 = FonteOrcamentaria.criar("1", "Fonte 1");
        FonteOrcamentaria fonte2 = FonteOrcamentaria.criar("2", "Fonte 2");

        //setar valores custo

        Custo.findOrCreate("2025", custoAquisicaoPCES)
             .setValores(26000000, 26000000, fonte1);
        Custo.findOrCreate("2026", custoAquisicaoPCES)
             .setValores(26000000, 0, fonte1);
        Custo.findOrCreate("2027", custoAquisicaoPCES)
             .setValores(26000000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoAquisicaoPMES)
            .setValores(80250000, 0, fonte1);
        Custo.findOrCreate("2026", custoAquisicaoPMES)
            .setValores(82250000, 0, fonte1);
        Custo.findOrCreate("2027", custoAquisicaoPMES)
            .setValores(72000000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoAquisicaoPCIES)
            .setValores(6000000, 0, fonte1);
        Custo.findOrCreate("2026", custoAquisicaoPCIES)
            .setValores(6000000, 0, fonte1);
        Custo.findOrCreate("2027", custoAquisicaoPCIES)
            .setValores(6000000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoAquisicaoFUNREBOM)
            .setValores(13823773.48, 0, fonte1);
        Custo.findOrCreate("2026", custoAquisicaoFUNREBOM)
            .setValores(19023774.48, 0, fonte1);
        Custo.findOrCreate("2027", custoAquisicaoFUNREBOM)
            .setValores(13587775.48, 0, fonte1);
            
        Custo.findOrCreate("2025", custoMacrodrenagemSEDURB)
        .setValores(109915909.28, 0, fonte1);
        
        Custo.findOrCreate("2025", custoCaisSECULT)
        .setValores(74633732.98, 0, fonte1);
        
        Custo.findOrCreate("2025", custoSinalizacaoDERES)
        .setValores(50000000, 50000000, fonte2);
        Custo.findOrCreate("2026", custoSinalizacaoDERES)
        .setValores(50000000, 50000000, fonte2);
        Custo.findOrCreate("2027", custoSinalizacaoDERES)
        .setValores(50000000, 50000000, fonte2);

        Custo.findOrCreate("2025", custoObrasSEAG)
        .setValores(8994010.17, 0, fonte1);
        Custo.findOrCreate("2026", custoObrasSEAG)
        .setValores(3800000, 0, fonte1);
        Custo.findOrCreate("2027", custoObrasSEAG)
        .setValores(3800000, 0, fonte1);

        Custo.findOrCreate("2025", custoCentroExceSESPORT)
        .setValores(2476310.12, 0, fonte1);
        
        Custo.findOrCreate("2025", custoCentroExcePROESPORTE)
        .setValores(1159032.68, 1159032.68, fonte2);
        
        Custo.findOrCreate("2025", custoGestaoBensSEJUS)
        .setValores(2500000, 0, fonte1);
        Custo.findOrCreate("2026", custoGestaoBensSEJUS)
        .setValores(2500000, 0, fonte1);
        Custo.findOrCreate("2027", custoGestaoBensSEJUS)
        .setValores(2500000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoGestaoBensPPES)
        .setValores(1895541.16, 0, fonte1);
        Custo.findOrCreate("2026", custoGestaoBensPPES)
        .setValores(1263694.11, 0, fonte1);
        Custo.findOrCreate("2027", custoGestaoBensPPES)
        .setValores(1263694.11, 0, fonte1);

        Custo.findOrCreate("2025", custoGestaoEspacoSECULT)
        .setValores(2000000, 0, fonte1);
        Custo.findOrCreate("2026", custoGestaoEspacoSECULT)
        .setValores(2000000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoProjEspeciaisSECULT)
        .setValores(2344000, 0, fonte1);
        Custo.findOrCreate("2026", custoProjEspeciaisSECULT)
        .setValores(2344000, 0, fonte1);
        Custo.findOrCreate("2027", custoProjEspeciaisSECULT)
        .setValores(2344000, 0, fonte1);
        
        Custo.findOrCreate("2025", custoConstCampoSESPORT)
        .setValores(12673860.73, 30000000, fonte1);
        Custo.findOrCreate("2025", custoConstCampoSESPORT)
        .setValores(500000, 500000, fonte2);
        Custo.findOrCreate("2026", custoConstCampoSESPORT)
        .setValores(0, 15000000, fonte1);
        Custo.findOrCreate("2027", custoConstCampoSESPORT)
        .setValores(0, 25000000, fonte1);
        
        Custo.findOrCreate("2025", custoNovoFundoFEADM)
        .setValores(12526388.07, 12526388.07, fonte1);
        Custo.findOrCreate("2026", custoNovoFundoFEADM)
        .setValores(240000000, 0, fonte1);
        Custo.findOrCreate("2027", custoNovoFundoFEADM)
        .setValores(240000000, 0, fonte1);

        Custo.findOrCreate("2025", custoArcoViarioDERES)
        .setValores(1243665.16, 1243665.16, fonte1);

        Custo.findOrCreate("2025", custoCarroTransSCM)
        .setValores(250000, 0, fonte1);

        // setar valors das execucoes
        ExecucaoOrcamentaria.findOrCreate("2025", planoAquisicaoFrota, uniPMES, invAquisicaoDeFrotas)
            .setValores(4377049, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoAquisicaoFrota, uniFUNREBOM, invAquisicaoDeFrotas)
            .setValores(2400000, 0, null, fonte2);

        ExecucaoOrcamentaria.findOrCreate("2025", planoCaisArtes, uniSECULT, invCaisArtes)
            .setValores(216000000, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoSinalizacao, uniDERES, invSinalizacoes)
            .setValores(34800000, 0, null, fonte2);

        ExecucaoOrcamentaria.findOrCreate("2025", planoObras, uniSEAG, invObras)
            .setValores(372481632, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoModeResso, uniSEDH, invModeResso)
            .setValores(2111050, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoGestaoBens, uniSEJUS, invGestaoBens)
            .setValores(14663926, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoGestaoBens, uniPPES, invGestaoBens)
            .setValores(645671, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoGestaoEspaco, uniSECULT, invGestaoEspaco)
            .setValores(3900000, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoConstrCampo, uniSESPORT, invConstrCampo)
            .setValores(50000, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoNovoFundo, uniFEADM, invNovoFundo)
            .setValores(24750000, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoCentroEventos, uniSEAG, invCentroEvento)
            .setValores(708000, 0, null, fonte1);

        ExecucaoOrcamentaria.findOrCreate("2025", planoProjResso, uniFUNPEN, invProjResso)
            .setValores(390000, 0, null, fonte2);

        ExecucaoOrcamentaria.findOrCreate("2025", planoArcoViario, uniDERES, invArcoViario)
            .setValores(250000, 0, null, fonte1);
        
    }

    private static Custo gerarCustoComObjeto(String nomeObj, String tipo, UnidadeOrcamentaria unidadeOrcamentaria, Conta conta){
        Objeto objeto = Objeto.criar(nomeObj, tipo, conta);
        
        String ano = String.valueOf(LocalDate.now().getYear()+1);
        
        Custo custo = Custo.criar(ano, objeto);

        return custo;

    }


}
