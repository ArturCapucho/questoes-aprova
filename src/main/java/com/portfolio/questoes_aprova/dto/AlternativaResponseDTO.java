package com.portfolio.questoes_aprova.dto;

import com.portfolio.questoes_aprova.entity.Alternativa;

public record AlternativaResponseDTO(Long id, Character letra, String texto, Boolean correta) {
    public static AlternativaResponseDTO from(Alternativa alternativa) {
        return new AlternativaResponseDTO(alternativa.getId(), alternativa.getLetra(), alternativa.getTexto(), alternativa.getCorreta());
    }
}
