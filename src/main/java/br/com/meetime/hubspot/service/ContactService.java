package br.com.meetime.hubspot.service;

import br.com.meetime.hubspot.dto.ContactRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContactService {

    private final TokenService tokenService;

    public ContactService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    public void criaContatoCRM(ContactRequest contactRequest, HttpServletResponse response) throws IOException {
        logger.info("Criando contato no HubSpot");

        String url = "https://api.hubapi.com/crm/v3/objects/contacts";

        Map<String, Object> properties = constroiContatoProperties(contactRequest);
        HttpHeaders headers = constroiHttpHeaders();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(properties, headers);
        RestTemplate restTemplate = new RestTemplate();

        int tentativas = 0;

        // Tenta criar o contato 3x, caso caia no erro 429 (too many requests)
        while (tentativas < 3) {
            tentativas++;
            try {
                ResponseEntity<String> hubSpotResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                if (hubSpotResponse.getStatusCode() == HttpStatus.CREATED) {
                    logger.info("Contato criado com sucesso!");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Contato " + contactRequest.firstname() + " criado com sucesso!");
                    return;
                } else {
                    logger.error("Erro ao criar contato: {}", hubSpotResponse.getStatusCode());
                    response.setStatus(hubSpotResponse.getStatusCode().value());
                    response.getWriter().write("Erro ao criar contato: " + hubSpotResponse.getStatusCode());
                    return;
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                int tempoEspera = obterTempoDeEspera(e);
                logger.warn("Limite de requisições atingido! Tentativa {}/{}. Aguardando {} milissegundos", tentativas, 3, tempoEspera);

                if (tentativas < 3) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(tempoEspera);  // Espera o tempo necessario ate tentar novamente criar o contato
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

        }

        logger.error("Falha após {} tentativas. Não foi possível criar o contato.", 3);
        response.setStatus(429);
        response.getWriter().write("Erro: Não foi possível criar o contato após várias tentativas. Tente novamente mais tarde.");
    }

    private Map<String, Object> constroiContatoProperties(ContactRequest contactRequest) {
        Map<String, Object> contact = new HashMap<>();
        contact.put("email", contactRequest.email());
        contact.put("firstname", contactRequest.firstname());
        contact.put("lastname", contactRequest.lastname());
        contact.put("phone", contactRequest.phone());

        Map<String, Object> properties = new HashMap<>();
        properties.put("properties", contact);
        return properties;
    }


    private HttpHeaders constroiHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.getAccessToken();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Calcula o tempo de espera baseado nos cabeçalhos
    private int obterTempoDeEspera(HttpClientErrorException.TooManyRequests e) {
        HttpHeaders responseHeaders = e.getResponseHeaders();

        if (responseHeaders.containsKey("X-HubSpot-RateLimit-Interval-Milliseconds") && responseHeaders.containsKey("X-HubSpot-RateLimit-Remaining")) {
            int intervalo = Integer.parseInt(responseHeaders.getFirst("X-HubSpot-RateLimit-Interval-Milliseconds"));
            int requisicoesRestantes = Integer.parseInt(responseHeaders.getFirst("X-HubSpot-RateLimit-Remaining"));

            if (requisicoesRestantes > 0) {
                return 0;  // Se ainda nao bateu o limite de requisicoes na janela de tempo, nao precisa esperar pra tentar novamente
            } else {
                return intervalo;  // Se tiver batido o limite de requisicoes, devolve o tempo de espera em milisegundos
            }
        }

        return 10000; // Caso os cabeçalhos não sejam devolvidos, espera a janela de tempo padrão dada pela api (10 segundos)
    }
}
