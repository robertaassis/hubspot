package br.com.meetime.hubspot.dto;

public record ContactRequest(String email,
                             String lastname,
                             String firstname,
                             String phone) {
}

