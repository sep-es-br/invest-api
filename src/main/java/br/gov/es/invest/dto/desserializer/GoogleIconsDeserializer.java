package br.gov.es.invest.dto.desserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GoogleIconsDeserializer extends StdDeserializer<List<String>>{

    private final List<String> whiteListCategorias = Arrays.asList(
        "Activities", "Common actions", "Hardware", "Photo&Image", "Social",
        "Transportation", "UI actions", "Business&Payments", "Communication",
        "Home"
    );

    public GoogleIconsDeserializer(){
        this(null);
    }

    public GoogleIconsDeserializer(Class<?> vc){
        super(vc);
    }

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        // TODO Auto-generated method stub
        
        ArrayList<String> iconesDisponiveis = new ArrayList<>();

        JsonNode root = p.getCodec().readTree(p);

        ArrayNode icones = (ArrayNode) root.get("icons");

        //remover os 33 primeiros icones que são inuteis
        for(int i = 0; i < 125; i++)
            icones.remove(0);

        for(JsonNode icone : icones) {

            ArrayNode unsupportedFamiliesNodes = (ArrayNode) icone.get("unsupported_families");
            ArrayNode categoriesNode = (ArrayNode) icone.get("categories");
            
            boolean encontrou = false;
            for(JsonNode family : unsupportedFamiliesNodes){
                if(family.asText().equals("Material Symbols Outlined"))
                encontrou = true;
            }
            if(!encontrou) {

                if(icone.get("name").asText().equals("description"))
                    System.out.println();

                for(JsonNode category : categoriesNode){
                    if(whiteListCategorias.contains(category.asText()))
                        encontrou = true;
                }

                if(encontrou)
                    iconesDisponiveis.add(icone.get("name").asText());
            }
        }
        
        return iconesDisponiveis;
    }
    
}
