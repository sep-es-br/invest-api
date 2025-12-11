package br.gov.es.invest.repository;

import br.gov.es.invest.model.Custo;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CustoRepository extends Neo4jRepository<Custo, Long> {
    
    @Query("MATCH\r\n" + //
                "    (fonte:FonteOrcamentaria)<-[indicadaPor:INDICADA_POR]-(custo:Custo)-[:ESTIMADO]->(p:Objeto)-[:CUSTEADO]->(conta:Conta)\r\n" + //
                "OPTIONAL MATCH (conta)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)\r\n" + //
                "OPTIONAL MATCH (conta)<-[:ORIENTA]-(plano:PlanoOrcamentario)\r\n" + //
                "WITH custo, fonte, unidade, plano, indicadaPor\r\n" + //
                "WHERE ( $idFonte IS NULL OR id(fonte) = $idFonte )\r\n" + //
                "    AND custo.anoExercicio = $exercicio\r\n" + //
                "    AND ( $idUnidade IS NULL OR id(unidade) = $idUnidade )\r\n" + //
                "    AND ( $idPlano IS NULL OR id(plano) = $idPlano )" + //
                "RETURN custo, collect(indicadaPor), collect(fonte)")
    public List<Custo> getTotais(Long idFonte, Integer exercicio, Long idUnidade, Long idPlano);

    @Query("MATCH (custo:Custo)\r\n" + //
            "RETURN DISTINCT custo.anoExercicio")
    public Set<Integer> getAnosExercicio();
    
    @Query("""
           MATCH (custo:Custo)-[:ESTIMADO]->(obj:Objeto)
           WHERE custo.anoExercicio = $anoExercicio AND id(obj) = $idObjeto
           RETURN id(custo)
           """)
    public Optional<Long> findIdByAnoExercicioObjetoId(Integer anoExercicio, Long idObjeto);
    
}
