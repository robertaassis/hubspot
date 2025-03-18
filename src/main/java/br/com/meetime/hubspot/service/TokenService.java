package br.com.meetime.hubspot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    // Recupera o access token do spring security
    public String getAccessToken() {
        logger.info("Token recuperado");
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
