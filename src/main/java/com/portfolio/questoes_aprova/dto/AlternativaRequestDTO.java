package com.portfolio.questoes_aprova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlternativaRequestDTO(
        @NotNull(message = "Letra da alternativa e obrigatoria.")
        Character letra,

        @NotBlank(message = "Texto da alternativa e obrigatorio.")
        String texto,

        @NotNull(message = "Informe se a alternativa esta correta.")
        Boolean correta
) {
}
