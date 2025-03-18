package br.com.meetime.hubspot.service;

import br.com.meetime.hubspot.dto.OAuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class AuthService {

    @Value("${spring.security.oauth2.client.registration.hubspot.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.hubspot.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.hubspot.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.registration.hubspot.redirect-uri}")
    private String redirectUrl;

    @Value("${spring.security.oauth2.client.provider.hubspot.authorization-uri}")
    private String authorizationUrl;

    @Value("${spring.security.oauth2.client.provider.hubspot.token-uri}")
    private String tokenUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public String geraUrlAutorizacao() {
        logger.info("Obtendo url de autorização");
        scope = scope.replace(",", " ");
        return authorizationUrl + "?client_id=" + clientId + "&scope=" + scope + "&redirect_uri=" + redirectUrl;
    }

    public OAuthResponse autenticaHubspot(String code, HttpServletResponse res) throws IOException {
        logger.info("Código recebido, buscando token de acesso");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", redirectUrl);
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try{
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<OAuthResponse> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, OAuthResponse.class);
            logger.info("Token de acesso recebido!");
            return response.getBody();
        } catch (Exception e){
            logger.error("Erro: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }
}
