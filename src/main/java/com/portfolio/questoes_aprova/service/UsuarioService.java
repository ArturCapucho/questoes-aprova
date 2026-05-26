package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.UsuarioRequestDTO;
import com.portfolio.questoes_aprova.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO criar(UsuarioRequestDTO dto);

    List<UsuarioResponseDTO> listar();
}
