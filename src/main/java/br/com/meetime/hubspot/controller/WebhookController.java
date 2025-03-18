package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.service.WebhookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    // Endpoint que escuta e processa eventos do tipo "contact.creation" enviados pelo webhook do HubSpot
    @PostMapping("/criacao-contato")
    public void processaEventoCriacaoContato(@RequestBody String payload) {
        webhookService.processaEventoCriacaoContato(payload);
    }
}
