package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.dto.ContactRequest;
import br.com.meetime.hubspot.service.ContactService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Cria contato no CRM
    @PostMapping
    public void criaContatoCRM(@RequestBody ContactRequest contactRequest, HttpServletResponse response) throws IOException {
        contactService.criaContatoCRM(contactRequest, response);
    }
}
