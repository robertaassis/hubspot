package br.com.meetime.hubspot.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SimpleBearerTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extraiToken(request);

        // Se o token não for enviado, retorna 403 Forbidden
        if (token == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Acesso negado: token não fornecido no header Authorization.");
            response.getWriter().flush();
            return;
        }

        try {
            SimpleBearerTokenAuthentication authentication = new SimpleBearerTokenAuthentication(token);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (HttpClientErrorException.Unauthorized ex) { // Se o token for inválido, retorna 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token inválido.");
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extraiToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }
}

