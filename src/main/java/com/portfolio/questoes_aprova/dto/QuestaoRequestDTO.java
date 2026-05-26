package com.portfolio.questoes_aprova.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestaoRequestDTO(
        @NotNull(message = "Ano e obrigatorio.")
        @Min(value = 1900, message = "Ano deve ser maior ou igual a 1900.")
        @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100.")
        Integer ano,

        @NotBlank(message = "Enunciado e obrigatorio.")
        String enunciado,

        @Size(max = 100, message = "Banca deve ter no maximo 100 caracteres.")
        String banca,

        @Size(max = 150, message = "Orgao deve ter no maximo 150 caracteres.")
        String orgao,

        @Size(max = 120, message = "Origem deve ter no maximo 120 caracteres.")
        String origem,

        @NotNull(message = "Categoria e obrigatoria.")
        Long categoriaId,

        @NotNull(message = "Alternativas sao obrigatorias.")
        @Valid
        @Size(min = 2, message = "Questao deve ter pelo menos duas alternativas.")
        List<AlternativaRequestDTO> alternativas
) {
}
