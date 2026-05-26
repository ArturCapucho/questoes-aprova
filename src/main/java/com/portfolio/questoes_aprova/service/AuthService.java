package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.AuthResponseDTO;
import com.portfolio.questoes_aprova.dto.LoginRequestDTO;

public interface AuthService {
    AuthResponseDTO autenticar(LoginRequestDTO dto);
}
