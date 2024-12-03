package br.gov.es.invest.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import br.gov.es.invest.dto.PapelDto;
import br.gov.es.invest.dto.SetorDto;
import br.gov.es.invest.dto.UnidadeOrcamentariaDTO;
import br.gov.es.invest.dto.acessocidadaoapi.PapelACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.SetorACResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.TokenResponseDto;
import br.gov.es.invest.dto.acessocidadaoapi.UnidadesACResponseDto;
import br.gov.es.invest.model.Orgao;
import net.minidev.json.JSONObject;

@Service
public class ACService {
    
  private static final String GUID_GOVES = "fe88eb2a-a1f3-4cb1-a684-87317baf5a57";

  @Value("${acessocidadao.tokenUrl}")
  private String ACTokenUrl;

  @Value("${acessocidadao.organogramaUrl}")
  private String organogramaUrl;

  @Value("${acessocidadao.webApiUrl}")
  private String webApiUrl;

  @Value("${acessocidadao.clientId}")
  private String clientId;

  @Value("${acessocidadao.secret}")
  private String clientSecret;

  @Value("${acessocidadao.scope}")
  private String scopes;


  public String getClientToken() {
    String basicToken = clientId + ":" + clientSecret;
    HttpClient httpClient = HttpClient.newHttpClient();

    String urlParameters  = "grant_type=client_credentials&scope=" + scopes;

    HttpRequest request;
    try {
        request = HttpRequest.newBuilder()
                                .header("Content-type", "application/x-www-form-urlencoded")
                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes()))
                                .uri(new URI(ACTokenUrl))
                                .POST(BodyPublishers.ofString(urlParameters)).build();

                                
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8")));

      
      if(response.statusCode() == HttpStatus.OK.value()) {
        TokenResponseDto tokenResponse = new JsonMapper().readValue(response.body(), TokenResponseDto.class);
        return tokenResponse.access_token();
      } else {
        Logger.getGlobal().severe(response.statusCode() + ": " + response.body());
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      Logger.getGlobal().info("ACTokenUrl: " + ACTokenUrl);
      e.printStackTrace();
    }



    return null;
  }

  public List<Orgao> getOrgaos(){

    String token = getClientToken();

    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest request;
    try {
        request = HttpRequest.newBuilder()
                                .header("Content-type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .uri(new URI(this.organogramaUrl + "/organizacoes/" + GUID_GOVES + "/filhas/"))
                                .GET().build();

                                  
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8")));

        if(response.statusCode() == HttpStatus.OK.value()) {
          List<UnidadesACResponseDto> UnidadesResponse = new JsonMapper().readValue(response.body(), new TypeReference<List<UnidadesACResponseDto>>(){});
          return UnidadesResponse.stream().map(unidadeResp -> new Orgao(unidadeResp)).toList();
        } else {
          Logger.getGlobal().severe("token: " + token);
          Logger.getGlobal().severe(response.statusCode() + ": " + response.body());
        }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      Logger.getGlobal().info("token: " + token);
      e.printStackTrace();
    }

    return null;
  }

  

  public List<SetorDto> getSetores(String unidadeGuid){

    String token = getClientToken();

    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest request;
    try {
        request = HttpRequest.newBuilder()
                                .header("Content-type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .uri(new URI(this.organogramaUrl + "/unidades/organizacao/" + unidadeGuid))
                                .GET().build();

                                  
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8")));

        if(response.statusCode() == HttpStatus.OK.value()) {
          List<SetorACResponseDto> setoresResponse = new JsonMapper().readValue(response.body(), new TypeReference<List<SetorACResponseDto>>(){});
          return setoresResponse.stream().map(setorResp -> new SetorDto(setorResp)).toList();
        } else {
          Logger.getGlobal().severe("token: " + token);
          Logger.getGlobal().severe(response.statusCode() + ": " + response.body());
        }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      Logger.getGlobal().info("token: " + token);
      e.printStackTrace();
    }

    return null;
  }

  public List<PapelDto> getPapeis(String setorGuid){

    String token = getClientToken();

    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest request;
    try {
        request = HttpRequest.newBuilder()
                                .header("Content-type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .uri(new URI(this.webApiUrl + "/conjunto/" + setorGuid + "/papeis"))
                                .GET().build();

                                  
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8")));

        if(response.statusCode() == HttpStatus.OK.value()) {
          List<PapelACResponseDto> papeisResponse = new JsonMapper().readValue(response.body(), new TypeReference<List<PapelACResponseDto>>(){});
          return papeisResponse.stream().map(papelResp -> new PapelDto(papelResp)).toList();
        } else {
          Logger.getGlobal().severe("token: " + token);
          Logger.getGlobal().severe(response.statusCode() + ": " + response.body());
        }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      Logger.getGlobal().info("token: " + token);
      e.printStackTrace();
    }

    return null;
  }
}
