/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author gean.carneiro
 */
public class ParticipeInterceptorConfig {
    
    @Value("${participe.clientCredential.clientId}")
    public String participeId;
    
    @Value("${participe.clientCredential.secret}")
    public String participeSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            // TODO Auto-generated method stub
            String notEncoded = participeId + ":" + participeSecret;
            String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

            template.header("Authorization", encodedAuth);
            
        };
    }
}
