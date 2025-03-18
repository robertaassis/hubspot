package br.com.meetime.hubspot.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SimpleBearerTokenAuthentication extends AbstractAuthenticationToken {

    private final String token;

    public SimpleBearerTokenAuthentication(String token) {
        super(null);
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
