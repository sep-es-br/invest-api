package br.gov.es.invest.service;

import br.gov.es.invest.dto.ValoresCusto;
import br.gov.es.invest.dto.desserializer.GoogleIconsDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InfosService {
    
    @Autowired
    private Neo4jOperations neo4jOperations;
    
    public List<String> getIconesDisponiveis() {
         HttpURLConnection con = null;
        try {
            URL googleIcons = new URL("https://fonts.google.com/metadata/icons?key=material_symbols&incomplete=true");

            con = (HttpURLConnection) googleIcons.openConnection();
            con.setRequestMethod("GET");

            if(con.getResponseCode() == HttpStatus.OK.value()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    String respLine;
                    StringBuffer response = new StringBuffer();
                    reader.readLine(); // "queima" a primeira linha que é lixo
                    while((respLine = reader.readLine()) != null) {
                        response.append(respLine);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule();
                    module.addDeserializer(List.class, new GoogleIconsDeserializer());
                    mapper.registerModule(module);

                    return (List<String>) mapper.readValue(response.toString(), List.class);
                    
                    
                }
            }

        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
        
        
        return Arrays.asList();
    }

    public ValoresCusto getTotaisInvestimento(String nome, Long idFonte, Integer exercicio, List<Long> idUnidade, List<Long> idPlano, Integer gnd){

        String cypher = """
                        MATCH (inv:Investimento)<-[:CUSTEADO]-(obj:Objeto)-[:EM]->(status:Status{statusId: 'CADASTRADO'}),
                                (po:PlanoOrcamentario)-[:ORIENTA]->(inv)<-[:IMPLEMENTA]-(unidade:UnidadeOrcamentaria)
                        WHERE 
                            ($idUnidade IS NULL OR id(unidade) IN $idUnidade)
                            AND ($idPlano IS NULL OR id(po) IN $idPlano)
                            AND ($nome IS NULL OR apoc.text.clean(inv.nome) CONTAINS apoc.text.clean($nome))
                        CALL (obj) {
                            MATCH (obj)<-[:ESTIMADO]-(custo:Custo)-[indicada_por:INDICADA_POR]->(fonteCusto:FonteOrcamentaria)
                            WHERE ($idFonte IS NULL OR id(fonteCusto) = $idFonte)
                                AND ($exercicio IS NULL OR custo.anoExercicio = $exercicio)
                                AND ($gnd IS NULL OR $gnd = indicada_por.gnd)
                            RETURN 
                                sum(indicada_por.planejado) AS totalPlanejado,
                                sum(indicada_por.contratado) AS totalContratado 
                        } 
                        RETURN
                                sum(totalPlanejado) AS planejado,
                                sum(totalContratado) AS contratado
                        """  ;

        HashMap<String, Object> params = new HashMap<>();
        params.put("idUnidade", idUnidade);
        params.put("idPlano", idPlano);
        params.put("nome", nome);
        params.put("idFonte", idFonte);
        params.put("exercicio", exercicio);
        params.put("gnd", gnd);

        return this.neo4jOperations.findOne(cypher, params, ValoresCusto.class).get();

    }





}
