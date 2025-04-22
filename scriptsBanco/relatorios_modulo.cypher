CREATE (mRelatorio:Modulo{
    nome: 'Relatório',
    pathId: 'relatorio'
}),
(mRelatorio)<-[:FILHO_DE]-(:Modulo{
    nome: 'Consolidado',
    pathId: 'relatorioconsolidado'
}),
(mRelatorio)<-[:FILHO_DE]-(:Modulo{
    nome: 'Detalhado',
    pathId: 'relatoriodetalhado'
})