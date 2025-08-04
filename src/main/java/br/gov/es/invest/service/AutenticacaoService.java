package br.gov.es.invest.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.invest.dto.ACUserInfoDto;
import br.gov.es.invest.dto.UsuarioDto;
import br.gov.es.invest.dto.acessocidadaoapi.OrganizacaoACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadeACResponseDto;
import br.gov.es.invest.exception.PapelInvalidoException;
import br.gov.es.invest.exception.UsuarioSemPermissaoException;
import br.gov.es.invest.exception.service.InfoplanServiceException;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.model.Papel;
import br.gov.es.invest.model.Setor;
import br.gov.es.invest.model.Usuario;
import br.gov.es.invest.repository.PapelRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final PapelRepository papelRepository;

    private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final ACService acService;
    private final PapelService papelSrv;
    private final GrupoService grupoService;
    private final SetorService setorService;
    private final OrgaoService orgaoService;

    @Value("${acessocidadao.webApiUrl}")
    private String webApiUrl;

    public UsuarioDto autenticar(String accessToken) {
        logger.info("Autenticar usuário SPO.");

        ACUserInfoDto userInfo = getUserInfo(accessToken);
        String token = tokenService.gerarToken(userInfo, accessToken);

        if(!userInfo.role().contains("GESTOR_MASTER") && !validarPapel(userInfo.subNovo())) throw new UsuarioSemPermissaoException();
        
        Usuario usuario = usuarioService.getUserBySub(userInfo.subNovo())
                            .orElseGet(() -> this.gerarUsuario(userInfo));

        if (usuario.getName() == null)
            usuario.setName(userInfo.apelido().split(" ")[0]);

        usuario.setNomeCompleto(userInfo.apelido());
        usuario.setEmail(getEmailUserInfo(userInfo));
        usuario.setRole(userInfo.role());
                
        usuario = usuarioService.save(usuario);
        
        UsuarioDto dto = UsuarioDto.parse(usuario,token);

        return dto;

    }

    protected boolean validaUsuario(Usuario user){
        if(user == null) {
            return false;
        }

        if(user.getPapeis() == null || user.getPapeis().isEmpty()) {
            Optional<Usuario> usuarioBanco = usuarioService.getUserBySub(user.getSub());

            if(usuarioBanco.isEmpty()) {
                return  false;
            } else {
                return !grupoService.getGruposDoUsuario( usuarioBanco.get().getId()).isEmpty();
            }
            
        } else {
            Papel papelDoUser = user.getPapeis().get(0);

            Papel papelProbe = new Papel();
            papelProbe.setGuid(papelDoUser.getGuid());

            Optional<Papel> optPapel = papelRepository.findBy(Example.of(papelProbe), q -> q.first());

            if(optPapel.isPresent()) {
                
                if(!grupoService.getGruposByPapel(optPapel.get().getId()).isEmpty()) return true;
                
                papelDoUser = optPapel.get();
            }
            Setor setorDoUser = papelDoUser.getSetor();

            Optional<Setor> optSetor = setorService.findByGuid(setorDoUser.getGuid());

            if(optSetor.isPresent()) {
                return !grupoService.getGruposBySetor(optSetor.get().getId()).isEmpty();
            } else {

                Orgao orgaoDoUser = setorDoUser.getOrgao();

                Optional<Orgao> optOrgao = orgaoService.findByGuid(orgaoDoUser.getGuid());

                if(optOrgao.isPresent()) {
                    return !grupoService.getGruposByOrgao(optOrgao.get().getId()).isEmpty();
                } else {
                    return false;
                }

            }




        }
        
    }

    protected Usuario gerarUsuario(ACUserInfoDto userInfo){
        
        String clientToken = acService.getClientToken();

        List<PapelACResponseDto> papeis = acService.getPapeisBySub(userInfo.subNovo(), clientToken);
        
        Usuario usuario = new Usuario(userInfo);
        usuario.setPapeis(papeis.stream()
                .map(papel -> acService.gerarPapelFromResp(papel, clientToken))
                .collect(Collectors.toList()));

        return usuario;
            
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
        
        for(PapelACResponseDto papelAc : papeisAc ){
            
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
