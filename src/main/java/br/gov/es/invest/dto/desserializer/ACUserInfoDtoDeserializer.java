package br.gov.es.invest.dto.desserializer;

import java.io.IOException;
import java.util.HashSet;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import br.gov.es.invest.dto.ACUserInfoDto;

public class ACUserInfoDtoDeserializer extends StdDeserializer<ACUserInfoDto> {

    public ACUserInfoDtoDeserializer() {
        this(null);
    }


    public ACUserInfoDtoDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public ACUserInfoDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        // TODO Auto-generated method stub

        JsonNode root = new ObjectMapper().readTree(p);

        String apelido = root.get("apelido").asText();
        Boolean cpfValidado = root.get("cpfValidado").asBoolean();
        Boolean verificada = root.get("verificada").asBoolean();
        String verificacaoTipo = root.get("verificacaoTipo").asText();
        String subNovo = root.get("subNovo").asText();
        Boolean agentepublico = root.get("agentepublico").asBoolean();
        String email = root.get("email").asText();
        String sub = root.get("sub").asText();

        JsonNode rolesNode = root.get("role");

        HashSet<String> roles = new HashSet<>();

        if(rolesNode != null) {
            if(rolesNode.isArray()) {
                ((ArrayNode) rolesNode).forEach(jn -> roles.add(jn.asText()));
            } else {
                roles.add(rolesNode.asText());
            }
        }


        return new ACUserInfoDto(apelido, cpfValidado, verificada, verificacaoTipo, subNovo, agentepublico, email, email, sub, roles);
        
    }
    
}
