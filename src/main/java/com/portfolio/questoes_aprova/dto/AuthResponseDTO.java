package com.portfolio.questoes_aprova.dto;

public record AuthResponseDTO(String token, String tipo) {
    public AuthResponseDTO(String token) {
        this(token, "Bearer");
    }
}
