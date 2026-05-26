package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.QuestaoRequestDTO;
import com.portfolio.questoes_aprova.dto.QuestaoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestaoService {
    QuestaoResponseDTO criar(QuestaoRequestDTO dto);

    QuestaoResponseDTO buscar(Long id, Integer ano);

    Page<QuestaoResponseDTO> listar(Pageable pageable);
}
