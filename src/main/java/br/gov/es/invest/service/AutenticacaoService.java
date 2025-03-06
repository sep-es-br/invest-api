package br.gov.es.invest.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.acessocidadaoapi.OrganizacaoACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import br.gov.es.invest.exception.PapelInvalidoException;
import br.gov.es.invest.exception.UsuarioInexistenteException;
import br.gov.es.invest.exception.UsuarioSemPermissaoException;
import br.gov.es.invest.exception.service.InfoplanServiceException;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.PapelRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final PapelRepository papelRepository;

    private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final ACService acService;
    private final GrupoService grupoService;

    @Value("${acessocidadao.webApiUrl}")
    private String webApiUrl;

    public UsuarioDto autenticar(String accessToken) {
        logger.info("Autenticar usuário SPO.");

        ACUserInfoDto userInfo = getUserInfo(accessToken);
        String token = tokenService.gerarToken(userInfo, accessToken);

        
        Optional<Usuario> optUsuario = usuarioService.getUserBySub(userInfo.subNovo());
        
        Usuario usuario;

        if(!userInfo.role().contains("GESTOR_MASTER") && optUsuario.isEmpty()){
            throw new UsuarioInexistenteException();
        } else if(optUsuario.isEmpty()) {
            usuario = new Usuario(userInfo);
        } else {
            usuario = optUsuario.get();

            if(!userInfo.role().contains("GESTOR_MASTER"))
                if(!validarPapel(usuario)) throw new PapelInvalidoException("Papel não existe ou não é prioritário");
            
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

    protected boolean validarPapel(Usuario user){
        
        ArrayList<PapelACResponseDto> papeisValidos = new ArrayList<>();

        String clientToken = acService.getClientToken();

        // 1º valida se o papel ainda existe e é prioritário
        if(user.getPapeis() == null) {
            List<PapelACResponseDto> papeisDoUser = acService.getPapeisBySub(user.getSub(), clientToken);
            
            List<PapelACResponseDto> papeisComNome = papeisDoUser.stream().filter(papelac -> papelac.Nome().equals(user.getPapel())).toList();
            if(papeisComNome.size() > 0){
                List<Papel> papeisPrioritário = papeisComNome.stream().filter(p -> p.Prioritario()).map(Papel::parse).toList();

                if(papeisPrioritário.size() > 0) {
                    papeisValidos.add(papeisComNome.getFirst());
                }

                // independentemente atualiza para novo formato
                Optional<PapelACResponseDto> optPapelCerto = papeisComNome.stream().filter(p -> p.LotacaoGuid().equals(user.getSetor().getGuid())).findFirst();

                if(optPapelCerto.isPresent()) {
                    Papel papel = Papel.parse(optPapelCerto.get());
                    papel.setSetor(user.getSetor());
                    
                    usuarioService.trasnferirNovoFormato(user, papel);
                }

                
            }
        } else {
            // pega o unico papel dele
            if (user.getPapeis().size() == 1){
                Papel papelNoBanco = user.getPapeis().getFirst();

                PapelACResponseDto papelAc = acService.getPapelByGuid(papelNoBanco.getGuid(), clientToken);
                if(papelAc != null && papelAc.Prioritario()){
                    Papel papelValido = Papel.parse(papelAc);
                    papelValido.setId(papelNoBanco.getId());
                    papelRepository.save(papelValido);
                    papeisValidos.add(papelAc);
                }

            } else { // ou, se tiver mais, valida todos os papeis
                
                for(Papel papel : user.getPapeis()){
                    PapelACResponseDto papelAc = acService.getPapelByGuid(papel.getGuid(), clientToken);
                    if(papelAc != null && papelAc.Prioritario()){
                        Papel papelValido = Papel.parse(papelAc);
                        papelValido.setId(papel.getId());
                        papelRepository.save(papelValido);
                        papeisValidos.add(papelAc);
                    } else if(papelAc != null) { // se não existir então remove
                        papelRepository.deleteById(papel.getId());
                    }

                }
            }

        }

        // valida se o setor e orgão ainda existem;
        ArrayList<PapelACResponseDto> papeisValidos2 = new ArrayList<>();
        for( PapelACResponseDto papel : papeisValidos ) {
            UnidadeACResponseDto unidade = acService.getUnidadeInfoByGuid(papel.LotacaoGuid(), clientToken);

            if(unidade != null ){
                OrganizacaoACResponseDto organizacaoACResponseDto = acService.getOrgaoInfoByGuid(unidade.guidOrganizacao(), clientToken);
                
                if( organizacaoACResponseDto != null){
                    papeisValidos2.add(papel);
                }
            }
        }

        return !papeisValidos2.isEmpty();
        
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
