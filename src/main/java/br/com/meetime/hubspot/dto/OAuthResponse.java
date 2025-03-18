package br.com.meetime.hubspot.dto;

public record OAuthResponse(String refresh_token,
                            String access_token,
                            long expires_in) {
}
