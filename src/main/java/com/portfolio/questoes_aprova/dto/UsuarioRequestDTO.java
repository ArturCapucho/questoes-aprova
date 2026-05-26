package com.portfolio.questoes_aprova.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome e obrigatorio.")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres.")
        String nome,

        @NotBlank(message = "E-mail e obrigatorio.")
        @Email(message = "E-mail deve ter formato valido.")
        String email,

        @NotBlank(message = "Senha e obrigatoria.")
        @Size(min = 6, max = 72, message = "Senha deve ter entre 6 e 72 caracteres.")
        String senha,

        String role
) {
}
