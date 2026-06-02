package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.DesempenhoResponseDTO;
import com.portfolio.questoes_aprova.dto.TentativaRequestDTO;
import com.portfolio.questoes_aprova.dto.TentativaResponseDTO;

public interface TentativaService {
    TentativaResponseDTO responder(String emailUsuario, TentativaRequestDTO dto);

    DesempenhoResponseDTO buscarDesempenho(String emailUsuario);
}
