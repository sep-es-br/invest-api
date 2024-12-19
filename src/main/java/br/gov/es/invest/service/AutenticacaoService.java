package br.gov.es.invest.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.exception.UsuarioInexistenteException;
import br.gov.es.invest.exception.UsuarioSemPermissaoException;
import br.gov.es.invest.exception.service.InfoplanServiceException;
import br.gov.es.invest.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public UsuarioDto autenticar(String accessToken) {
        logger.info("Autenticar usuário SPO.");

        ACUserInfoDto userInfo = getUserInfo(accessToken);
        String token = tokenService.gerarToken(userInfo);

        
        Usuario usuario = usuarioService.getUserBySub(userInfo.subNovo());
        
        if(usuario == null){
            throw new UsuarioInexistenteException();
        }

        if (usuario.getName() == null)
            usuario.setName(userInfo.apelido().split(" ")[0]);

        usuario.setNomeCompleto(userInfo.apelido());
        usuario.setEmail(getEmailUserInfo(userInfo));
        usuario.setRole(userInfo.role());
                
        usuario = usuarioService.save(usuario);
        
        UsuarioDto dto = new UsuarioDto(usuario);
        dto.setToken(token);

        return dto;


    }

    protected ACUserInfoDto getUserInfo(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://acessocidadao.es.gov.br/is/connect/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            ACUserInfoDto userInfoDto;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            userInfoDto = new ObjectMapper().readValue(response.body(), ACUserInfoDto.class);

            if(userInfoDto.role().size() == 0) {
                throw new UsuarioSemPermissaoException();
            }

            return userInfoDto;
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        throw new InfoplanServiceException(List.of("Não foi possível identificar um usuário no acesso cidadão com esse token. Faça login novamente!"));
    }

    protected ACUserInfoDto getUserPermissions(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://acessocidadao.es.gov.br/is/connect/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            ACUserInfoDto userInfoDto;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            userInfoDto = new ObjectMapper().readValue(response.body(), ACUserInfoDto.class);

            return userInfoDto;
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        throw new InfoplanServiceException(List.of("Não foi possível identificar um usuário no acesso cidadão com esse token. Faça login novamente!"));
    }

    private static String getEmailUserInfo(ACUserInfoDto userInfo) {
        return userInfo.emailCorporativo() != null ? userInfo.emailCorporativo() : userInfo.email();
    }
}
