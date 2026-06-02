package com.portfolio.questoes_aprova.dto;

import jakarta.validation.constraints.NotNull;

public record TentativaRequestDTO(
        @NotNull(message = "Questao e obrigatoria.")
        Long questaoId,

        @NotNull(message = "Ano da questao e obrigatorio.")
        Integer questaoAno,

        @NotNull(message = "Alternativa escolhida e obrigatoria.")
        Long alternativaEscolhidaId
) {
}
