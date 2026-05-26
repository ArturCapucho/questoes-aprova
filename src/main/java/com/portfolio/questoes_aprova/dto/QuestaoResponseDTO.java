package com.portfolio.questoes_aprova.dto;

import com.portfolio.questoes_aprova.entity.Questao;

import java.util.List;
import java.util.UUID;

public record QuestaoResponseDTO(
        Long id,
        Integer ano,
        UUID uuid,
        String enunciado,
        String banca,
        String orgao,
        String origem,
        CategoriaResponseDTO categoria,
        List<AlternativaResponseDTO> alternativas
) {
    public static QuestaoResponseDTO from(Questao questao) {
        List<AlternativaResponseDTO> alternativas = questao.getAlternativas() == null
                ? List.of()
                : questao.getAlternativas().stream().map(AlternativaResponseDTO::from).toList();

        return new QuestaoResponseDTO(
                questao.getId(),
                questao.getAno(),
                questao.getUuid(),
                questao.getEnunciado(),
                questao.getBanca(),
                questao.getOrgao(),
                questao.getOrigem(),
                CategoriaResponseDTO.from(questao.getCategoria()),
                alternativas
        );
    }
}
