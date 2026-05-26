package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.TentativaRequestDTO;
import com.portfolio.questoes_aprova.dto.TentativaResponseDTO;

public interface TentativaService {
    TentativaResponseDTO responder(TentativaRequestDTO dto);
}
