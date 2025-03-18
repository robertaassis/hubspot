package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.dto.OAuthResponse;
import br.com.meetime.hubspot.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint responsavel por gerar e retornar a URL de autorizacao
    @GetMapping("/url-autorizacao")
    public String geraUrlAutorizacao() {
        return authService.geraUrlAutorizacao();
    }
    // Endpoint recebe o código de autorização fornecido pelo HubSpot, realiza a troca pelo token de acesso e armazena o token no cookie
    @GetMapping("/redireciona")
    public void autenticaHubspot(@RequestParam String code, HttpServletResponse response, HttpServletRequest request) throws IOException {

        OAuthResponse authResponse = authService.autenticaHubspot(code, response);

        if (authResponse != null && authResponse.access_token() != null) {
            String accessToken = authResponse.access_token();
            response.getWriter().write("Token de acesso: " + accessToken +"\nUse-o para colocar na header authorization (Bearer token) ao criar um contato no Postman.");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autorização falhou: token inválido");
        }
    }
}
