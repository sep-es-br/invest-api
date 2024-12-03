package br.gov.es.invest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ACService {
    
    @Value("acessocidadao.tokenUrl")
    private String ACTokenUrl;

    @Value("acessocidadao.clientId")
    private String clientId;

    @Value("acessocidadao.secret")
    private String secret;

    private static final String GUID_GOVES = "fe88eb2a-a1f3-4cb1-a684-87317baf5a57";



}
