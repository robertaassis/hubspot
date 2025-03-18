package br.com.meetime.hubspot.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    public void processaEventoCriacaoContato(String payload) {
        logger.info("Webhook recebido: {}", payload);
    }
}
