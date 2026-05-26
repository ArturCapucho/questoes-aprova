package com.portfolio.questoes_aprova.dto;

import com.portfolio.questoes_aprova.entity.Tentativa;

import java.time.OffsetDateTime;

public record TentativaResponseDTO(
        Long id,
        Long usuarioId,
        Long questaoId,
        Integer questaoAno,
        Long alternativaEscolhidaId,
        Boolean correta,
        String explicacaoIa,
        OffsetDateTime respondidoEm
) {
    public static TentativaResponseDTO from(Tentativa tentativa) {
        return new TentativaResponseDTO(
                tentativa.getId(),
                tentativa.getUsuario().getId(),
                tentativa.getQuestao().getId(),
                tentativa.getQuestao().getAno(),
                tentativa.getAlternativaEscolhida().getId(),
                tentativa.getCorreta(),
                tentativa.getExplicacaoIa(),
                tentativa.getRespondidoEm()
        );
    }
}
