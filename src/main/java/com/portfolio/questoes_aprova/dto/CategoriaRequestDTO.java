package com.portfolio.questoes_aprova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(
        @NotBlank(message = "Nome da categoria e obrigatorio.")
        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres.")
        String nome,

        @NotBlank(message = "Slug da categoria e obrigatorio.")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug deve usar letras minusculas, numeros e hifens.")
        @Size(max = 100, message = "Slug deve ter no maximo 100 caracteres.")
        String slug,

        String descricao,
        Long categoriaPaiId
) {
}
