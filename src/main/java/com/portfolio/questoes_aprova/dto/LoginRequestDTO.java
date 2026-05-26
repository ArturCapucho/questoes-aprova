package com.portfolio.questoes_aprova.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "E-mail e obrigatorio.")
        @Email(message = "E-mail deve ter formato valido.")
        String email,

        @NotBlank(message = "Senha e obrigatoria.")
        String senha
) {
}
