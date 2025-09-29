package br.gov.es.invest.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final ClientRegistrationRepository clientRegistrationRepository;
        private final SecurityFilter securityFilter;

        @Value("${frontend.host}")
        private String frontendUrl;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authConfig -> {
                        authConfig.requestMatchers(HttpMethod.GET,
                                "/swagger-ui.html",
                                "/swagger-ui/*",
                                "/v3/*",
                                "/v3/api-docs/*",
                                "/signin/**",
                                "/acesso-cidadao-response.html",
                                "/execucao/importarPentaho").permitAll();

                        authConfig.anyRequest().authenticated();
                })
                .oauth2Login(oAuth2LoginConfig ->
                        oAuth2LoginConfig.authorizationEndpoint(authEndpointConfig ->
                                authEndpointConfig.authorizationRequestResolver(new AuthorizationRequestResolver(
                                        clientRegistrationRepository, "/oauth2/authorization")))
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .exceptionHandling(Customizer.withDefaults())
                .build();
        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
                return new WebMvcConfigurer() {
                        @Override
                        public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
                                registry.addMapping("/**").allowedOrigins(frontendUrl)
                                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                                                .allowedHeaders("*")
                                                .allowCredentials(true)
                                                .exposedHeaders("Content-Disposition");
                        }
                };
        }
    


}