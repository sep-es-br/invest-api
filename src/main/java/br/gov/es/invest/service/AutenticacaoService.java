package br.gov.es.invest.service;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import br.gov.es.invest.exception.UsuarioSemPermissaoException;
import br.gov.es.invest.exception.service.InfoplanServiceException;
import br.gov.es.invest.model.Agente;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final ACService acService;
    private final PapelService papelSrv;
    private final GrupoService grupoService;
    private final SetorService setorService;
    private final OrgaoService orgaoService;

    public UsuarioDto autenticar(String accessToken) {
        logger.info("Autenticar usuário SPO");

        ACUserInfoDto userInfo = getUserInfo(accessToken);
        String token = tokenService.gerarToken(userInfo, accessToken);
        
        String acToken = acService.getClientToken();

        if(!userInfo.role().contains("GESTOR_MASTER") && !validarPapel(userInfo.subNovo())) throw new UsuarioSemPermissaoException();

        
        List<Papel> papeis = acService.getPapeisBySub(userInfo.subNovo(), acToken).stream()
                                .map(papel -> acService.gerarPapelFromResp(papel, acToken))
                                .toList();
        
        
        Agente usuario = usuarioService.getUserBySub(userInfo.subNovo())
                            .orElseGet(() -> new Agente(userInfo));
        
        usuario.setDeletadoEm(null);

        if (usuario.getName() == null)
            usuario.setName(userInfo.apelido().split(" ")[0]);

        usuario.setNomeCompleto(userInfo.apelido());
        usuario.setEmail(getEmailUserInfo(userInfo));
        usuario.setRole(userInfo.role());
        usuario.setPapeis(papeis);
                
        usuario = usuarioService.save(usuario);
        
        UsuarioDto dto = UsuarioDto.parse(usuario,token);

        return dto;

    }

    protected boolean validarPapel(String userSub){
        

        String clientToken = acService.getClientToken();
        List<PapelACResponseDto> papeisAc = acService.getPapeisBySub(userSub, clientToken);
        
        /**
         * niveis de validação de cada papel
         * 
         * 1) papel: se o papel é cadastrado diretamente em algum grupo
         * 2) setor: se o setor do papel está cadastrado
         * 3) orgão: se o orgão tá cadastrado
         * 
         * objetivo dessa etapa não é definir acessos especificos, é apenas saber se tem ou não algum acesso
         * acesso especifico é validado em seus respectivos módulos
         * 
         */
        
        List<PapelACResponseDto> papeisPrioritarios = papeisAc.stream().filter(p -> p.Prioritario()).toList();
        
        for(PapelACResponseDto papelAc : papeisPrioritarios.isEmpty() ? papeisAc : papeisPrioritarios ){
            
            Optional<Papel> papelBanco = papelSrv.findByGuid(papelAc.Guid());
            
            if(papelBanco.isPresent()){
                if(!grupoService.getGruposByPapel(papelBanco.get().getId()).isEmpty())
                    return true;
            }
            
            if(papelAc.LotacaoGuid() != null) {
                
                UnidadeACResponseDto setorAc = acService.getUnidadeInfoByGuid(papelAc.LotacaoGuid(), clientToken);
                
                if(setorAc != null){
                    
                    Optional<Setor> setorBanco = setorService.findByGuid(setorAc.guid());
                    
                    if(setorBanco.isPresent()){
                        if(!grupoService.getGruposBySetor(setorBanco.get().getId()).isEmpty())
                            return true;
                    }
                    
                    if(setorAc.guidOrganizacao() != null){
                        
                        Optional<Orgao> orgaoBanco = orgaoService.findByGuid(setorAc.guidOrganizacao());
                        
                        if(orgaoBanco.isPresent()){
                            if(!grupoService.getGruposByOrgao(orgaoBanco.get().getId()).isEmpty())
                                return true;
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        return false;
        
        
    }
    
//    public void transferirTodosUsuarios(){
//        List<Usuario> usuarios = usuarioService.findAll();
//        String token = acService.getClientToken();
//        
//        for(Usuario user : usuarios){
//            if(user.getPapeis() != null && !user.getPapeis().isEmpty()) continue;
//            
//            List<PapelACResponseDto> papeisAc = acService.getPapeisBySub(user.getSub(), token);
//            
//            user.setPapeis(papeisAc.stream().map(papel -> acService.gerarPapelFromResp(papel, token)).toList());
//            user.setPapel(null);
//            user = usuarioService.save(user);
//            for(Papel papel : user.getPapeis()){
//                usuarioService.transferirGrupo(user.getId(), papel.getId());
//            }
//            
//        }
//    }

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

    private static String getEmailUserInfo(ACUserInfoDto userInfo) {
        return Optional.ofNullable(userInfo.emailCorporativo()).orElse(userInfo.email());
    }
}
